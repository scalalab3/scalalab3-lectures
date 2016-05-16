
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

object RoutingExample {

  // for run many instances of one actor
  case class Message(id: Int)
  class MyActor extends Actor {
    def receive = {
      case Message(id) => sender ! self.path
    }
  }

  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val timeout = Timeout(1 second)

  // how ?
  import akka.routing.RoundRobinPool
  val myActorRouter = system.actorOf(
    Props[MyActor].withRouter(RoundRobinPool(3))
  )

  (1 to 6).foreach { i =>
    (myActorRouter ? Message(i)).map { id => println(s"----> $id") }
  }

  /*  output:
    ----> akka://default/user/$a/$a
    ----> akka://default/user/$a/$c
    ----> akka://default/user/$a/$a
    ----> akka://default/user/$a/$b
    ----> akka://default/user/$a/$b
    ----> akka://default/user/$a/$c
   */


  system.scheduler.scheduleOnce(3 seconds) { system.shutdown() }


}
