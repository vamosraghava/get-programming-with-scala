package org.example.books.entities

import java.time.Year

case class Book(id: Long, title: String, authors: List[String], year: Year)

sealed trait Genre
case object Drama extends Genre
case object Horror extends Genre
case object Romantic extends Genre
