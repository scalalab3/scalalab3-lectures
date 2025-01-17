import sbt._
import sbt.Keys._

object ScalalabBuild extends Build with SbtUtils {

  lazy val lectures_di = project("patterns", "lectures/patterns").settings(
    Seq(libraryDependencies ++= mongo)
  )

  lazy val lectures_shapeless = project("shapeless", "lectures/shapeless")
      .settings(
        Seq(libraryDependencies ++= Seq(shapeless) ++ mongo)
      )

  lazy val lectures_fs = project("futures-and-streams", "lectures/futures-and-streams")
    .settings(
      Seq(libraryDependencies ++= akka ++ Seq(async))
    )

  lazy val lectures_actors = project("actors", "lectures/actors").settings(
    Seq(libraryDependencies ++= akka)
  )

  lazy val lectures_scalaz = project("scalaz", "lectures/scalaz").settings(
    Seq(libraryDependencies ++= Seq(scalaz))
  )

  lazy val main = project("scalalab3", ".").settings(
    Seq(
      name := "scalalab3",
      libraryDependencies ++= Seq()
    )
  ) dependsOn (
    lectures_di,
    lectures_shapeless,
    lectures_fs,
    lectures_actors,
    lectures_scalaz
  )


}
