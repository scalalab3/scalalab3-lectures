package com.github.fntz.scalalab3.di

object CakePattern {

  trait filling
  case object chocolate extends filling


  object _step0 {
    // i want to see recipe for each cake, with different ingredients

    trait Filling {
      def ingredients: String
      def fillingType: filling
    }

    trait Cake {
      def gerRecipe: String
    }

  }

  // How to implement this in scala?

  object _step1 {
    // yep, simple inheritance:
    trait Filling {
      def ingredients: String = s"$fillingType: [...]"
      def fillingType: filling
    }

    trait Cake extends Filling {
      def getRecipe: String = ingredients
    }

    trait Chocolate extends Filling {
      override val fillingType = chocolate
    }

    val cake = new Cake with Chocolate

    println(cake.getRecipe)

    // and now: Cake = Ingredient :) !!!
  }


  object _step2 {
    // composition
    trait Filling {
      def ingredients: String = s"$fillingType: [...]"
      def fillingType: filling
    }

    trait Cake {
      def ingredient: Filling
      def getRecipe: String = ingredient.ingredients
    }

    trait Chocolate extends Filling {
      override val fillingType = chocolate
    }

    val cake = new Cake {
      override def ingredient: Filling = new Chocolate {}
    }
    println(cake.getRecipe)

    // cake is not ingredient !
    // but if we have state in Filling, and we re-use Filling in another class ???
  }

  object _step3 {
    // composition++
    trait Filling {
      def ingredients: String = s"$fillingType: [...]"
      def fillingType: filling
    }

    trait Cake { self: Filling => // composition
      def getRecipe: String = self.ingredients
    }

    trait Chocolate extends Filling {
      override val fillingType = chocolate
    }

    object ChocolateCake extends Cake with Chocolate // inheritance
    // singleton object :(
    println(ChocolateCake.getRecipe)

    // note about self-type annotation
    trait A { }
    trait B { self : A => } // copy all methods from A to B
    // possible use with Types:
    trait C[T] {}
    trait D[T] { self : A with C[T] => }
    // and structural types:
    trait E { self : { def log(x: String): Unit; def count: Int } => }

  }

  object _step4 {
    trait FillingComponent {
      trait Filling {
        def ingredients: String = s"$fillingType: [...]"
        def fillingType: filling
      }

      protected val ingredient: Filling

      class Chocolate extends Filling {
        override val fillingType = chocolate
      }

    }

    trait CakeComponent {
      self: FillingComponent => // depends on Filling

      trait Cake {
        def getRecipe: String
      }

      protected val cake: Cake
      protected class ChocolateCake extends Cake {
        def getRecipe: String = ingredient.ingredients
      }
    }

    class MyCake extends CakeComponent with FillingComponent {
      override val ingredient = new Chocolate
      override val cake = new ChocolateCake
    }
    val cake = new MyCake
    println(cake.cake.getRecipe)

    // summary:
    // + components
    // + compile-time guarantees
    // - hard
    // - initialization troubles (but the same possible in inheritance variant)

  }

  _step1
  _step2
  _step3
  _step4


}



