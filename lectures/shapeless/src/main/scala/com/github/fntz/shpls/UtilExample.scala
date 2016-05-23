package com.github.fntz.shpls

object UtilExample {

  case class Address(street: String)
  case class Person(id: Int, name: String, address: Address)

  import shapeless.lens

  val root = lens[Person]

  val idLens = root.id
  val nameLens = root >> 'name
  val streetLens  = root >> 'address >> 'street

  val nameAndStreetLens = nameLens ~ streetLens

  val p = Person(1, "John Doe", Address("Foo bar"))

  // get
  println(s"get id: ${idLens.get(p)}") // => get id: 1

  // set
  val p1 = idLens.set(p)(10)
  println(s"get id: ${idLens.get(p1)}") // => get id: 10

  // combine get
  val p2 = nameAndStreetLens.set(p)(("New name", "Baz"))

  println(s"name+street: ${nameAndStreetLens.get(p2)}") // => name+street: (New name,Baz)

  // ===== typeable
  // type safe cast instead of `asInstanceOf`
  import shapeless.syntax.typeable._
  val x: Any = p
  println(x.cast[Person]) // => Some(Person(1,John Doe,Address(Foo bar)))

  // more operations! (https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.1.0)
  import shapeless.syntax.std.tuple._
  import shapeless.Poly1
  object inc extends Poly1 {
    implicit def caseInt    = at[Int](x => x + 1)
    implicit def default[T] = at[T](identity)
  }
  val t = (1, "John Doe", 10d)
  val nt = t.map(inc)
  println(nt) // => (2,John Doe,10.0)


}
