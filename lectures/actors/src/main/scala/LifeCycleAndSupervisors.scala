
import akka.actor._
import scala.concurrent.duration._

object LifeCycleAndSupervisors {

  implicit val system = ActorSystem()
  import system.dispatcher

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._

  case object RestartMessage
  case object StopMessage
  case object EscalateMessage
  case object ResumeMessage


  class ChActor extends Actor {

    override def preStart = { println("pre start") }
    override def postStop = { println("post stop") }
    override def preRestart(reason: Throwable, message: Option[Any]) = {
      println(s"pre restart reason: ${reason.getMessage}")
    }
    override def postRestart(reason: Throwable) = {
      println(s"post restart")
    }


    def receive = {
      case ResumeMessage   => throw new ArithmeticException("boom!")
      case RestartMessage  => throw new NullPointerException("boom!")
      case StopMessage     => throw new IllegalArgumentException("boom!")
      case EscalateMessage => throw new RuntimeException("boom!")
    }
  }

  class MyActor extends Actor {

    override val supervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 1 second) {
        case _: ArithmeticException      =>
          println("=== resume")
          Resume
        case _: NullPointerException     =>
          println(s"=== restart")
          Restart
        case _: IllegalArgumentException =>
          println("=== stop")
          Stop
        case _: Exception                =>
          println("=== escalate")
          Escalate // = Restart
      }

    val ref = context.actorOf(Props[ChActor])
    context.watch(ref) // monitoring actor

    def receive = {
      case Terminated(actor) =>
        println(s"$actor stopped")
        // => Actor[akka://default/user/$a/$a#-6829393] stopped

      case x => ref ! x
    }

  }

  val myActor = system.actorOf(Props[MyActor])
//  myActor ! Message(0)
  /*
   pre start
   === resume
  */

//  myActor ! Message(1)
  /*
    pre start
    === restart
    pre restart reason: boom!
    post restart
   */

//  myActor ! Message(10)
  /*
    pre start
    === escalate
    post stop
    pre start
   */

//  myActor ! StopMessage
  /*
    pre start
    === stop
    post stop
   */

  myActor ! RestartMessage
  myActor ! RestartMessage
  myActor ! RestartMessage
  myActor ! RestartMessage // => dead letters





  system.scheduler.scheduleOnce(10 seconds) { system.shutdown() }

}
