package org.example.books

import org.example.books.entities.Book

import scala.util.Try

// book catalog parsing
  // list of books
  // map in the parser
  // try in parsing from file

class BookParser(filePath: String) {

  private def parseFromFile: List[Book] = ???

  private def parseRow: Try[Book] = ???
  // here you will have maps

  val books: List[Book] = ???
}
