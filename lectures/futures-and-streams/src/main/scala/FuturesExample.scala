import scala.concurrent.ExecutionContext

object FuturesExample {

  /*
    java:
      old java api: Thread
      new java api: Executor#run(Runnable r)
        ExecutorService#submit
                       #awaitTermination
                       #shutdown
                       #shutdownNow
                       ...

   */


  def submitForm: String = {
    Thread.sleep(50)
    java.util.UUID.randomUUID().toString
  }

  def submitFormException: String = {
    throw new RuntimeException("oops")
  }

  object futures {
    import scala.concurrent.Future

    // just for example (not use it in production)
    import scala.concurrent.ExecutionContext.Implicits.global

    object step1 {
      // our program
      // blocking call
      // ... action
      val result = submitForm
      println(s"===> result(1)=$result")
    }

    object step2 {
      // ... action
      // naive implementation
      val result = Future(submitForm)
      Thread.sleep(100)

      if (result.isCompleted) {
        // result.value: Option[Try[String]
        println(s"===> result(2)=${result.value.get.get}")
      } else {
        println("failed") // result is None
      }
    }

    object step3 {
      // callback
      import scala.util.{Success, Failure}
      // ... action
      val result = Future(submitForm)
      result.onComplete {
        case Success(r) => println(s"===> result(3)=$r")
        case Failure(e) => println(s"failed: ${e.getMessage}")
      }
    }

    object step4 {
      // Future is monadic like structure
      Future { submitForm }.map { result =>
        println(s"===> result(4)=$result")
      }

      // use for () yield
      for {
        result <- Future(submitForm)
      } yield println(s"===> result(4.1)=$result")

      // run in parallel
      val future1 = Future(submitForm)
      val future2 = Future(submitForm)
      for {
        result1 <- future1
        result2 <- future2
      } yield println(s"result(4.2)=$result1 & result(4.3)=$result2")

    }

    object step5 {
      // recover
      val f = Future(submitFormException)
      f.map { result =>
        println(s"result(5)=$result")
      }.recover {
        case x: Throwable => println(s"failed: ${x.getMessage}")
      }

      val f1 = Future(submitFormException)
      f1.failed.foreach {
        x =>
          println(s"foreach: $x")
      }

      // when exception not important for us
      val f2 = Future(submitFormException)
      f2.fallbackTo(Future.successful("uuid")).foreach { result =>
        println(s"result(fallback)=$result")
      }

    }

    object step6 {
      // Future of Future
      Future(Future(submitForm)).flatMap(identity)

      // from List[Futures] to Future[List]
      Future.sequence {
        List(
          Future(submitForm),
          Future(submitForm),
          Future(submitForm)
        )
      }.foreach { xs => // List[String]

      }

    }

    object step7 {
      // hints
      // 1. don't use `scala.concurrent.ExecutionContext.Implicits.global`

      // 2. use custom execution context
      implicit val ec = ExecutionContext.fromExecutorService(
        java.util.concurrent.Executors.newCachedThreadPool()
      )

      // 3. use Futures for IO operations
      Future { 1 + 1 } // bad
      // instead of it use
      Future.successful(1 + 1)

      // 4. be careful with `scala.concurrent.blocking`

      ec.shutdown()

    }

    step1
    step2
    step3
    step4
    step5
    step6
    step7
  }

  object promises {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.Promise

    // what difference between promise and future ?
    // future - tasks without our control (network operation for example)
    // promise - tasks with our control
    // see example
    val p = Promise[String]() // <- create promise
    // each promise have
    val f = p.future
    // just subscribe for complete
    f.onComplete {
      case x => println(s"wow! $x")
    }
    // and complete promise
    p.success(submitForm)

    // promises for convert callback api into future api
  }

  object awaits {
    // Await is bad!
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.{Await, Future}
    import scala.concurrent.duration._

    // block thread
    val result = Await.result(Future(submitForm), 150 millis)
    println(s"===> result(Await)=$result")
  }

  object extensions {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.async.Async.{async, await}
    import scala.concurrent.Future
    async {  // <- Future[String]
      submitForm
    }.foreach { result =>
       println(s"===> result(async)=$result")
    }

    async {
      val f1 = await(Future(submitForm))
      val f2 = await(Future(submitForm))
      s"$f1 & $f2"
    }.foreach { result =>
      println(s"complex: $result")
    }

  }


  futures
  promises
  awaits
  extensions

  Thread.sleep(1500)



}
