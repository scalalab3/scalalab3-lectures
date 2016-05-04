import sbt._
import sbt.Keys._

trait Deps {
  
  object V {
    val shapelessVersion = "2.3.0"
    val mongoVersion = "3.1.1"
    val akkaVersion = "2.3.9"
  }

  import V._

  val shapeless =
    "com.chuusai" %% "shapeless" % shapelessVersion

  val mongo = Seq(
    "org.mongodb" % "casbah-commons_2.11" % mongoVersion,
    "org.mongodb" % "casbah-core_2.11" % mongoVersion
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-experimental" % "2.0.3"
  )

  val libs = Seq(

  )


}

