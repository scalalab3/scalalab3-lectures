import sbt._
import sbt.Keys._

trait SbtUtils extends Deps {

  val projectSettings = Seq(
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xlint",
      "-Xfuture",
      "-Yno-adapted-args",
      "-Ywarn-dead-code"
    ),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += "bintray/non" at "http://dl.bintray.com/non/maven",
    scalaVersion := "2.11.6"
  )

  def project(id: String, f: String) =
    Project(id, file(f))
      .settings(projectSettings)

}
