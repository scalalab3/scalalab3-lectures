
import akka.actor.ActorSystem


object ActorSystemExample {

  // create
  implicit val system = ActorSystem("my-system")

  // stop
  system.shutdown()

}
