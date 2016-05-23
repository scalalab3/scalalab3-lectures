package com.github.fntz.shpls



object HlistExample {
  import shapeless._
  import shapeless.ops.hlist.IsHCons

  /**
    * HList it's a like List but without type inference?
    * You save not only value, but and type of this value
    */

  // usual scala list:
  // val l = 1 :: "asd" :: Nil // => warning: a type was inferred to be `Any`;
                            // this may indicate a programming error.

  // hlist:
  import shapeless.HNil
  val hl = 1 :: "asd" :: HNil

  /**
    * Possible use different methods for HList, like in usual scala list
    * see `shapeless.ops.hlist._` package
    */

  // For example get by Number:
  println(hl.at(0)) // => 1
  // and the same as in Coproduct, hl.at(3) does not compile

  // or
  println(hl take 1) // => 1 :: HNil
  // hl take 10 // does not compile

  /**
    * Fetch Strings from HList
    */

  val hls = 1 :: "foo" :: "bar" :: 10 :: HNil

  trait LowPriorityImplicits extends Poly2 {
    implicit def default[Element, L <: HList] = at[Element, L]((_, list) => {
      list
    })
  }

  object takeString extends LowPriorityImplicits {
    implicit def whenString[Acc, L <: HList] = at[String, L]((element, acc) => {
      element :: acc
    })
  }

  println(hls.foldRight(HNil)(takeString)) // => foo :: bar :: HNil

  /**
    * Every time takeString take a Accumulator and HList and if
    * types do not match, we call LowPriorityImplicits#default method
    *
    */

  /**
    * Ok, let's implement some toy methods for HList
    * In shapeless manner
    */

  final class MyHListAdditionalMethods[L <: HList](val l: L) {
    def stringify(implicit stringify: Stringify[L]): stringify.Out = stringify(l)
  }

  // Trait define Input and Output types (for us Output is a HList)
  trait Stringify[L <: HList] {
    type Out <: HList
    def apply(l: L): Out
  }

  // Companion object contains
  // type Alias - Aux (see about path depends types)
  // and methods for processing Input type
  object Stringify {
    def apply[I <: HList, O <: HList](implicit s: Stringify.Aux[I, O]) = s

    type Aux[I <: HList, O <: HList] = Stringify[I] { type Out = O }

    // Define when HList is Empty (HNil)
    implicit def stringifyHNil: Stringify.Aux[HNil, HNil] = new Stringify[HNil] {
      type Out = HNil
      def apply(l: HNil) = l
    }

    // When HList with one element (element + HNil)
    implicit def stringifyHead[L]: Stringify.Aux[L :: HNil, String :: HNil] =
      new Stringify[L :: HNil] {
        type Out = String :: HNil
        def apply(l: L :: HNil) = l.head.toString :: HNil
      }

    // When HList with many elements
    implicit def stringifyTail[L <: HList, H, T <: HList, X <: HList](
      implicit c: IsHCons.Aux[L, H, T], s: Stringify.Aux[T, X]
    ): Stringify.Aux[L, String :: X] =
      new Stringify[L] {
        type Out = String :: X
        def apply(l: L) = {
          l.head.toString :: l.tail.stringify
        }
      }
  }

  implicit def hlistOps1[L <: HList](l: L): MyHListAdditionalMethods[L] =
    new MyHListAdditionalMethods(l)

  val l1 = 1 :: 10 :: HNil // => shapeless.::[Int,shapeless.::[Int,shapeless.HNil]]
  val l2 = l1.stringify    // => shapeless.::[String,shapeless.::[String,shapeless.HNil]]

  // This is all what you need know about HList

}
