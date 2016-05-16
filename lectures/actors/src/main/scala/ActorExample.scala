import akka.actor._

import scala.concurrent.duration._
import scala.concurrent.Await

// actors and event bus
object ActorExample {

  case class Message(d: Double)
  case object Kick
  case object Ok

  class MyActor extends Actor {
    def receive = {
      case x: String =>
        // self is not this
        println(s"MyActor (path: $self) -> $x")

      case x: Int => sender ! (x + 1)

      case Message(x) =>
        println(s"==== block on $x message")
        Thread.sleep(100)

      case Kick =>
        println("ok :(")
        context.stop(self)


    }

    override def postStop = {
      println(s"stop $self")
    }

    override def preStart = {
      println(s"start $self")
    }

    override def preRestart(reason: Throwable, message: Option[Any]) = {
      println(s"pre restart $self because ${reason.getMessage} with $message")
    }

  }

  implicit val system = ActorSystem()
  import system.dispatcher

  // create actor instance
  val myActor = system.actorOf(Props[MyActor])
  val myActor1 = system.actorOf(Props(classOf[MyActor]))
  val myActor2 = system.actorOf(Props(classOf[MyActor]))
  // or with parameters for constructor
  // val myActor = system.actorOf(Props(classOf[MyActor], params))

  // myActor is actorRef

  // send message to actor (fire and forget)
  myActor ! "message"

  // ask patter, when you want to receive response
  import akka.pattern.ask
  import akka.util.Timeout
  implicit val timeout = Timeout(1 second)
  (myActor ? 9).mapTo[Int].map { x => println(s"result=$x") }

  // how to stop ?
  // Kill generates ActorKillException

  myActor ! Message(1d)
  myActor ! Kill
  myActor ! Message(1d)   // => deadletter


  myActor1 ! Message(10d)
  myActor  ! Message(10d) // => deadletter
  myActor1 ! PoisonPill

  // graceful stop
  import akka.pattern.gracefulStop
  gracefulStop(myActor2, 1 second, Kick).map { result =>
    println(s"gracefulStop => $result")
  }




  // event bus for send message to N actors
  // use `system.eventStream`
  // or `context.system.eventStream` inside actor

  import akka.actor.DeadLetter

  class DeadLetterHandler extends Actor {
    def receive = {
      case DeadLetter(msg, from, to) =>
        println(s"Dead letter -----> $msg from $from to $to")
    }
  }

  val dlh = system.actorOf(Props[DeadLetterHandler])

  // just subscribe special class `akka.actor.DeadLetter`
  system.eventStream.subscribe(dlh, classOf[DeadLetter])



  system.scheduler.scheduleOnce(3 seconds){ system.shutdown() }

}
