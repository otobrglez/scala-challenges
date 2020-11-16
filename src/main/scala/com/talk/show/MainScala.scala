package com.talk.show

import java.time.{LocalDate, LocalDateTime}

object MainScala extends App {

  // OOP
  class Person(name: String, bornOn: LocalDate)

  class Student(name: String, bornOn: LocalDate) extends Person(name, bornOn) {
    override def toString: String = s"Hello I'm ${name} born on ${bornOn}"
  }

  class Professor(name: String, bornOn: LocalDate) extends Person(name, bornOn) {
    override def toString: String = s"Hello I'm ${name} born on ${bornOn} PROFESSOR"
  }

  var jernej = new Student("Jernej", LocalDate.parse("1985-06-17"))
  var me = new Professor("Oto", LocalDate.parse("1987-06-17"))
  var blendor = new Student("Blendor", LocalDate.parse("1994-06-17"))

  var people: List[Person] = List(jernej, me, blendor)
  people = people :+ new Professor("Dodo", LocalDate.parse("1994-06-17"))

  people.take(2).foreach { person =>
    println(person)
  }

  // Interop
  println(LocalDateTime.now())
  println(System.getProperty("java.version"))

  var addressBook = Map[String, Person](("oto" -> me), ("blendor" -> blendor), ("jernej" -> jernej))

  println(addressBook.get("oto"))
  println(addressBook.get("dddddddd"))

  // Case classes
  case class User(email: String, id: Int)

  case class Address(street: String)

  val u = User("otobrglez@gmail.com", 42)

  println("------")
  val result: Option[Person] = addressBook.get("oto")
  result match {
    case Some(person) => println(s"Hello person ${person}")
    case None => println("nema ovog tukaj")
  }
}