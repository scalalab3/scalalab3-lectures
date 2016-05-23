package com.github.fntz.shpls

object CoproductExample {

  /**
    *
    * Sometimes when we work with different business requirements
    * we are faced with troubles, which very difficult to solve
    * in familiar manner for us
    *
    * Let's talk about Person, which has hobby
    *
    * case class Person(name: String, interests: ???)
    *
    * If it's person, without any interests then it simple
    *
    * p1 = Person("name-1", interests = "reading")
    *
    * That simple, right?
    *
    *
    * But, what's about many-sided man
    *
    * p2 = Person("name-2", interests = ["reading", "fishing", "soccer"]
    *
    * No, it's not many-sided, but:
    *
    * case object All
    *
    * p3 = Person("name-3", interests = All)
    *
    * What to do?
    *
    * We had an `interests` field, which possible String or Collection of String or All (object)
    *
    *
    * Meet - shapless.Coproduct
    *
    * Coproduct it's like Either, but more, because Either it's a just Left or Right
    *
    */

  import shapeless.{CNil, :+:, Coproduct}

  case object All

  type Interests = String :+: List[String] :+: All.type :+: CNil

  case class Person(name: String, interests: Interests)

  /**
    * Ok, we create a type alias via `type` keyword, which
    * composed from 3 different types (String, List[String] and All.type)
    *
    * How to use it?
    *
    */

  val p1_i = Coproduct[Interests]("reading")
  val p2_i = Coproduct[Interests]("reading" :: "fishing" :: Nil)
  val p3_i = Coproduct[Interests](All)


  val p1 = Person("name-1", p1_i)
  val p2 = Person("name-2", p2_i)
  val p3 = Person("name-3", p3_i)

  /**
    * How to extract necessary value?
    *
    * By type!
    */

  println(p1_i.select[String])       // => Some(reading)
  println(p1_i.select[List[String]]) // => None
  println(p1_i.select[All.type])     // => None

  /**
    * What are operations supported by Coproduct?
    *
    * See shapeless.ops.coproduct._ package
    *
    * Just for example
    */

  // At extract value by Number from Coproduct like in collection
  println(p1_i.at(0)) //  => Some(reading)
  // but
  // p1_i.at(3) // => does not compile :)


  // See PolyApp examples
  import shapeless.Poly1

  val fishing = "fishing"
  object addFishing extends Poly1 {
    implicit def caseString = at[String](x => x :: fishing :: Nil)
    implicit def caseList   = at[List[String]](xs => xs :+ fishing)
    implicit def caseAll    = at[All.type](identity)
  }

  val p1_i_d = p1_i.map(addFishing)
  println(p1_i_d.at(0)) // => Some(List(reading, drinking))


}
