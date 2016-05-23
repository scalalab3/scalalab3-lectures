package com.github.fntz.shpls

object GenericExample {

  /**
    * Our case classes is a just HList
    * Because every case class is a list of types
    *
    * case class Person(id: Option[String], name: String, age: Int)
    * it's like a
    *
    * Option[String] :: String :: Int :: HNil
    *
    * right?
    *
    * This is that in shapeless known as Generic Representation (Repr)
    *
    * trait Generic[T] extends Serializable {
    *   type Repr
    *   def to(t : T) : Repr
    *   def from(r : Repr) : T
    * }
    *
    * Convert case class to Repr
    *
    */
  import shapeless.Generic
  import shapeless.LabelledGeneric
  import shapeless.HNil

  case class Person(id: Option[String], name: String, age: Int)
  val person = Person(None, "John Doe", 20)
  val personGen = Generic[Person]
  println(personGen.to(person)) // => None :: John Doe :: 20 :: HNil
  // materialize Person from HList
  val personHList = Some("key") :: "Name" :: 19 :: HNil
  println(personGen.from(personHList)) // => Person(Some(key),Name,19)

  import shapeless.record._

  val personLGen = LabelledGeneric[Person]
  val personRecord = personLGen.to(person)
  println(personRecord('name)) // => John Doe

  // println(personRecord('foo)) does not compile

}
