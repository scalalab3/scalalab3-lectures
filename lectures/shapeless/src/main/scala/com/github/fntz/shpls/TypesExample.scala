package com.github.fntz.scalalab3.shpls

object TypesExample {

  object singletonTypes {
    object step1 {
      def fooOnly(x: String) = {
        if (x != "foo") {
          throw new RuntimeException("boom!")
        } else {
          println(x)
        }
      }
    }

    object step2 {
      object O {
        val singetonFoo = "foo"
      }

      def fooOnly(x: O.type) = {
        println(O.singetonFoo)
      }
    }

    object step3 {
      import shapeless._
      import shapeless.syntax.singleton._

      val singletonFoo = "foo".narrow

      def fooOnly(x: singletonFoo.type) = {
        println(x)
      }
    }

  }

  object phatnomTypes {
    object step1 {
      class QuertyBuilder {
        def select(params: String) = this
        def from(table: String) = this
        def where(cond: String) = this
      }

      val query = new QuertyBuilder()
      query.select("id").from("persons").where("id = 10")
      query.from("persons").where("id = 10") // runtime exception
    }

    object step2 {
      sealed trait State
      trait Start extends State
      trait Select extends State
      trait From extends State
      trait Where extends State

      class QuertyBuilder[T <: State] private () {
        def select(params: String)(implicit ev: T =:= Start) = new QuertyBuilder[Select]
        def from(table: String)(implicit ev: T =:= Select) = new QuertyBuilder[From]
        def where(cond: String)(implicit ev: T =:= From) = new QuertyBuilder[Where]
      }
      object QuertyBuilder {
        def apply() = new QuertyBuilder[Start]
      }

      import shapeless.test._

      val query = QuertyBuilder()

      illTyped("""query.from("id").where("id = 10")""") // does not compile
      query.select("id").from("persons").where("id = 10")
    }
  }

  singletonTypes
  phatnomTypes

}
