package com.github.fntz.shpls



object PolyExample {

  /**
    * scala Function it's object under the hood
    *
    * is not possible define lambda function with the same name
    *
    *  val inc = (x: Int) => x + 1
    *  val inc = (x: Double) => x + 1
    *
    */

  object LikePolyFunction {
    def apply(x: Int) = println(s"int: $x")
    def apply(x: String) = println(s"string: $x")
  }

  import shapeless.test.illTyped

  LikePolyFunction(1)
  LikePolyFunction("foo")
  illTyped("""LikePolyFunction(1d)""") // => does not compile

  import shapeless.Poly1

  // doesn't make sense, only for example
  object isString extends Poly1 {
    implicit def whenInt = at[Int](_ => false)
    implicit def whenString = at[String](x => true)

    implicit def default[T] = at[T](_ => false)
  }

  println(s"is string: ${isString(1)}")  // => false
  println(s"is string: ${isString(1d)}") // => false
  println(s"is string: ${isString("foo")}") // => true


}
