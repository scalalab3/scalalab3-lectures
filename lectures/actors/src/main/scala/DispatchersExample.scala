
import akka.actor._
import scala.concurrent.duration._

object DispatchersExample extends App {

  implicit val system = ActorSystem()
  import system.dispatcher
  val dn = "dispatchers.one-thread-dispatcher"

  // http://doc.akka.io/docs/akka/snapshot/scala/dispatchers.html
  // Dispatcher
  // PinnedDispatcher
  // BalancingDispatcher

  class MyActor extends Actor {
    def receive = {
      case _ =>
        println("blocking call")
        Thread.sleep(4000)
    }
  }

  class AnotherActor extends Actor {
    def receive = {
      case x =>
        println(s"===> $x")
    }
  }

  val myActor = system.actorOf(Props[MyActor].withDispatcher(dn))
  val anotherActor = system.actorOf(Props[AnotherActor].withDispatcher(dn))

  myActor ! "foo"
  (1 to 10).foreach { x => anotherActor ! x }

  // Futures in dispatcher. WARNING!
  class MyActor1 extends Actor {
    import context.dispatcher // <- all futures inside actor run in dispatcher
    def receive = {
      case _ =>
    }
  }


  system.scheduler.scheduleOnce(7 seconds) { system.shutdown() }
}
