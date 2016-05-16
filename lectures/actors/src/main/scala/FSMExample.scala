
import akka.actor._
import scala.concurrent.duration._

object FSMExample {

  implicit val system = ActorSystem()
  import system.dispatcher

  // fsm in actors
  // via context.become
  case class Message(id: Int)
  class HttpActor extends Actor {
    def receive = {
      case Message(id) => sender ! (id + 1)
    }
  }


  class MyActor extends Actor {

    val http = context.actorOf(Props[HttpActor])

    def sendRequest: Receive = {
      case x: Message =>
        http ! x
        context.become(waitResponse)
    }

    def waitResponse: Receive = {
      case x =>
        println(s"result = $x")
        context.unbecome()
    }

    def receive = sendRequest
  }

  val myActor = system.actorOf(Props[MyActor])
  myActor ! Message(9)

  // Note
  // if we try to send another Message, probably we get "result = Message(9)"

  class A extends Actor {
    def receive = {
      case Message(id) => sender ! Message(id + 1)
    }
  }

  import akka.actor.FSM
  // FSM = State + Data
  object MyFSM {
    sealed trait State
    case object Idle extends State
    case object WaitResponse extends State
    case object FinishStep extends State

    sealed trait Data
    case object Uninitialized extends Data
    case class Context(id: Int) extends Data
  }

  class MyFSM extends FSM[MyFSM.State, MyFSM.Data] {
    import MyFSM._

    startWith(Idle, Uninitialized)

    val a = context.actorOf(Props[A])

    var originalSender: ActorRef = _

    when(Idle) {
      case Event(_, _) =>
        originalSender = sender
        a ! Message(1)
        goto(WaitResponse) using Context(10)
    }

    when(WaitResponse) {
      case Event(Message(id), context: Context) =>
        self ! Message(id)
        goto(FinishStep)
    }

    when(FinishStep) {
      case Event(Message(id), _) =>
        println(s"=== result: $id")
        stop
    }

    initialize()
  }

  val myFSM = system.actorOf(Props[MyFSM])
  myFSM ! "start"


  system.scheduler.scheduleOnce(3 seconds) { system.shutdown() }

}
