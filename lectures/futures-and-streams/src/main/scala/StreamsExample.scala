
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.concurrent.duration._


object StreamsExample {

  implicit val system = ActorSystem()
  import system.dispatcher
  system.scheduler.scheduleOnce(10 seconds){ system.shutdown() }
  implicit val mat = ActorMaterializer()

  // Source: data Input into Stream

  // create
  val source = Source(1 to 3)
  val source1 = Source.fromIterator(() => Iterator.from(0))
  val source2 = Source.fromFuture(Future.successful("ok"))

  // Source for http request, db query, ...

  // Sink <- data Output: for write operations, like write in db, file ...
  val sink = Sink.foreach[Int](x => println(s"-> $x")) // <- Sink[Int, Future[Unit]]

  // Source + Sink = RunnableGraph
  val runnableGraph = source to sink // <- RunnableGraph[Unit]
  runnableGraph.run()
  source.runForeach(println) // <- materialize !

  // `materialize` always start with `run*` methods

  //sink in actor !
  import akka.actor.ActorDSL._
  val ref = actor(new Act {
    become {
      case x => println(s"actor -> $x")
    }
  })

  val sinkRef = Sink.actorRef(ref, "stop!")

  (source to sinkRef).run
  /* output:
    actor -> 1
    actor -> 2
    actor -> 3
    actor -> stop!
   */

  /*
    summary:

    Source <- input data (output in stream)
    Sink <- output for data (input in `other` world)
    Source + Sink <- minimum stream, RunnableGraph

   */

  // Flow !

  /*
   flow like a different filter(for first view) methods
   Source -> Flow -> Flow -> ... -> Flow -> Sink
   Flow had input and output
   Some math:

   Source + Flow = Source
   Flow + Sink = Sink

   Source + Flow + Sink = Runnable Flow

   Flow like a `|` in linux shell

  */

  val flow1 = Flow[Int].map(x => x * 10)
  val flow2 = Flow[Int].map(x => x - 1)
  println("=== flow ")
  source via flow1 via flow2 to sink run
  /*
  source | flow1 | flow2 >> file.txt
   */

  // via = Source + Flow

  // stream transformation
  // 1. like in collections: filter, map, fold
  // 2. time based: takeWithin, *Within ???
  // 3. rate: expand, conflate, buffer
  // 4. async: mapAsync, *Async

  // GraphDSL example
  /*
    Need:
      create post request (name & email) then send
      write result in bd
      send email

      in flow

       email
       name -> create request -> send -> write in bd
                                 &
                                 send email

   */

  case class RequestResult(result: String)
  val emailSource = Source.fromFuture(Future.successful("email@email.com"))
  // possible from source, but not necessary
  val g = RunnableGraph.fromGraph(GraphDSL.create(emailSource) { implicit builder => email =>
    import GraphDSL.Implicits._
    val name = Source.single("name")
    val zip = builder.add(Zip[String, String])
    val broadcast = builder.add(Broadcast[RequestResult](2))

    val requestResult = Flow[(String, String)].map { case _ @ (a, b) =>
      RequestResult(s"$a: $b")
    }

    val sendEmail = Sink.foreach[RequestResult](x => println(s"send email: $x"))
    val insertInDb = Sink.foreach[RequestResult](x => println(s"insert $x"))

    name  ~> zip.in0
    email ~> zip.in1
    zip.out ~> requestResult ~> broadcast ~> sendEmail
    broadcast ~> insertInDb

    ClosedShape
  })
  g.run

  // materializer - run streams in actor system (actors under the hood)
  // run with run* methods
  // nothing more

  // Error handling:
  // Use Supervision Strategy for it
  // resume or restart or stop or custom
  object example {
    val supervisor: Supervision.Decider = {
      case _ => Supervision.Resume
    }
    implicit val mat = ActorMaterializer(
      ActorMaterializerSettings(system)
        .withSupervisionStrategy(supervisor)
    )

    val sourceN = Source(-1 to 1)
    sourceN.map(x => 1 / x).runForeach(x => println(s"/ => $x"))
    // output: -1, 1
  }

  example


}
