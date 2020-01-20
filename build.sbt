import NpmWebpack._
import scala.sys.process._

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-hello-world-tutorial""",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      ws,
      ehcache,
      "org.jsoup" % "jsoup" % "1.11.3",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )

lazy val buildFrontend = taskKey[Unit]("Execute frontend scripts")

buildFrontend := {
  val s: TaskStreams = streams.value
  val shell: Seq[String] = if (sys.props("os.name").contains("Windows")) Seq("cmd", "/c") else Seq("bash", "-c")
  val npmCi: Seq[String] = shell :+ "npm ci"
  //val npmTest: Seq[String] = shell :+    "npm run test"
  //val npmLint: Seq[String] = shell :+    "npm run lint"
  val npmBuild: Seq[String] = shell :+   "npm run build-prod"
  s.log.info("building frontend...")
  if((npmCi #&& /*npmTest #&& npmLint #&&*/ npmBuild !) == 0) {
    s.log.success("frontend build successful!")
  } else {
    throw new IllegalStateException("frontend build failed!")
  }
}

(packageSrc in Compile) := ((packageSrc in Compile) dependsOn buildFrontend).value

PlayKeys.playRunHooks += (baseDirectory.map(NpmWebpack.apply)).value