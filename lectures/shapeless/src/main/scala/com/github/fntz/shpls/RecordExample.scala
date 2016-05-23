package com.github.fntz.shpls

object RecordExample {

  import shapeless.record._
  import shapeless.syntax.singleton._
  import shapeless.HNil

  val record = ("id" ->> 1) :: ("name" ->> "John Doe") :: HNil

  println(record("id")) // => 1

  println(record.keys) // id :: name :: HNil

  // update
  val newRecord = record + ("id" ->> 10)

  println(newRecord("id")) // => 10

  import shapeless.Poly1
  import shapeless.labelled.{FieldType, field}

  // map over record
  object upd extends Poly1 {
    implicit def whenString[K] = at[FieldType[K, String]](_ =>
      field[K]("new name")
    )
    implicit def default[T] = at[T](identity)
  }

  val updRecord = record.map(upd)
  println(updRecord("name"))  // => new name

}
