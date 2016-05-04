package com.github.fntz.scalalab3.di

object TypeClasses {

  // i hate toString :)

  case class Person(id: Int, name: String)
  val person = Person(1, "x")

  object step1 {
    trait Show {
      def show: String
    }
    def p[T <: Show](x: T) = println(x.show)

    // in user code
    case class Person(id: Int, name: String) extends Show {
      def show = toString
    }
    val person = Person(1, "x")

    p(person)

  }

  object step2 {
    trait Show[T] {
      def show(x: T): String
    }

    def p[T](x: T)(implicit s: Show[T]) = println(s.show(x))

    /*
    could not find implicit value for parameter
    s: Show[com.github.fntz.scalalab3.di.TypeClasses.Person]
     */

    implicit val personShow = new Show[Person] {
      def show(x: Person) = s"Person[id=${x.id} & name=${x.name}]"
    }

    p(person) // => Person[id=1 & name=x]

    implicit object intShow extends Show[Int] {
      def show(x: Int) = s"Int=$x"
    }

    p(1) // => Int=1
  }

  // some sugar
  object step3 {

    def p[T : Show : Show1](x: T) =
      println(implicitly[Show[T]].show(x) + implicitly[Show1[T]].show1(x))

    trait Show[T] {
      def show(x: T): String
    }

    trait Show1[T] {
      def show1(x: T): String
    }


    implicit val personShow = new Show[Person] {
      def show(x: Person) = s"Person[id=${x.id}"
    }

    implicit val personShow1 = new Show1[Person] {
      def show1(x: Person) = s" & name=${x.name}]"
    }

    p(person) // Person[id=1 & name=x]

  }

  step1
  step2
  step3


}
