package org.example.books

import java.time.Year

import org.example.books.entities._

class BookService(bookCatalogPath: String) {

  private val books: List[Book] = new BookParser(bookCatalogPath).books

  private var bookLoans: Set[BookLoan] = Set.empty

  def search(title: Option[String] = None,
             author: Option[String] = None,
             year: Option[Year] = None): List[Book] =
    books.filter { book =>
      title.forall(t => book.title == t) &&
      author.forall(a => book.authors.contains(a)) &&
      year.forall(y => book.year == y)
    }

  def reserveBook(bookId: Long, user: User): Either[String, BookLoan] =
    for {
      _ <- checkReserveLimits(user)
      book <- checkBookExists(bookId)
      _ <- checkBookIsAvailable(book)
    } yield registerBookLoan(book, user)

  def returnBook(bookId: Long): Either[String, BookLoan] =
    for {
      book <- checkBookExists(bookId)
      user <- checkBookIsTaken(book)
    } yield unregisterBookLoan(book, user)

  private val loanLimit = 5
  private def checkReserveLimits(user: User): Either[String, User] = {
    if (bookLoans.count(_.user == user) <= loanLimit) Right(user)
    else Left(s"You cannot loan more than $loanLimit books")
  }

  private def checkBookExists(bookId: Long): Either[String, Book] =
    findBookById(bookId) match {
      case Some(book) => Right(book)
      case None => Left(s"Book with id $bookId not found")
    }

  private def checkBookIsAvailable(book: Book): Either[String, Book] =
    findBookLoan(book) match {
      case Some(_) => Left("Another user has loaned the book")
      case None => Right(book)
    }

  private def checkBookIsTaken(book: Book): Either[String, User] =
    findBookLoan(book) match {
      case Some(BookLoan(_, user)) => Right(user)
      case None => Left(s"Book ${book.id} does not result out on loan")
    }

  private def findBookById(id: Long): Option[Book] = books.find(_.id == id)

  private def findBookLoan(book: Book): Option[BookLoan] = bookLoans.find(_.book == book)

  private def registerBookLoan(book: Book, user: User): BookLoan = {
    val bookLoan = BookLoan(book, user)
    synchronized { bookLoans = bookLoans + bookLoan }
    bookLoan
  }

  private def unregisterBookLoan(book: Book, user: User): BookLoan = {
    val bookLoan = BookLoan(book, user)
    synchronized { bookLoans = bookLoans - bookLoan }
    bookLoan
  }

}
