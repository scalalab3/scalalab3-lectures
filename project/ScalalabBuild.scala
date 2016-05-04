import sbt._
import sbt.Keys._

object ScalalabBuild extends Build with SbtUtils {

  lazy val lectures_di = project("patterns", "lectures/patterns").settings(
    Seq(libraryDependencies ++= mongo)
  )

  lazy val lectures_shpls = project("shpls", "lectures/shpls")
      .settings(
        Seq(libraryDependencies ++= Seq(shapeless) ++ mongo)
      )

  lazy val lectures_fs = project("futures-and-streams", "lectures/futures-and-streams")
    .settings(
      Seq(libraryDependencies ++= akka)
    )

  lazy val main = project("scalalab3", ".").settings(
    Seq(
      name := "scalalab3",
      libraryDependencies ++= Seq()
    )
  ) dependsOn (lectures_di, lectures_shpls, lectures_fs)


}
