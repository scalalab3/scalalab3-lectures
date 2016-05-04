package com.github.fntz.scalalab3.di


object PimpMyLibrary {

  // compiler output: scalac -Xprint:22

  object step1 {
    class StringExt(x: String) {
      def isFoo = x.toLowerCase == "foo"
    }

    implicit def stringOps(x: String): StringExt = new StringExt(x)

    println("foo".isFoo) // => true

    /* COMPILER OUTPUT

    implicit def stringOps(x: String): StringExt = new StringExt(x);

    ... isFoo impl

    def isFoo(): Boolean = StringExt.this.x.toLowerCase().==("foo")

    ... println

    scala.this.Predef.println(scala.Boolean.box(stringOps("foo").isFoo()))

     */


  }

  object step2 {

    class StringExt(val x: String) extends AnyVal {
      def isFoo = x.toLowerCase == "foo"
    }

    implicit def stringOps(x: String): StringExt = new StringExt(x)

    println("foo".isFoo) // => true

    /* COMPILER OUTPUT


    implicit def stringOps(x: String): String = x

    ... isFoo impl

    final def isFoo$extension($this: String): Boolean = $this.toLowerCase().==("foo")

    ... println

    scala.this.Predef.println(scala.Boolean.box(StringExt.this.isFoo$extension(stringOps("foo"))))

     */

  }

  // new approach
  object step3 {
    implicit class StringExt(x: String) {
      def isFoo = x.toLowerCase == "foo"
    }

    println("a".isFoo)

    /* COMPILER OUTPUT

      as in step 2

    */

  }

  step1
  step2
  step3


}