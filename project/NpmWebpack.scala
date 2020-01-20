import java.net.InetSocketAddress

import play.sbt.PlayRunHook
import sbt._

import scala.sys.process.Process

object NpmWebpack {
  def apply(base: File): PlayRunHook = {
    object WebpackHook extends PlayRunHook {
      var process: Option[Process] = None

      val shell: Seq[String] = if (sys.props("os.name").contains("Windows")) Seq("cmd", "/c") else Seq("bash", "-c")
      val npmInstall: Seq[String] = shell :+ "npm install"
      val npmTest: Seq[String] = shell :+    "npm run test"
      val npmLint: Seq[String] = shell :+    "npm run lint"
      val npmBuildDev: Seq[String] = shell :+   "npm run build:dev"
      val npmRunDev: Seq[String] = shell :+   "npm run watch:dev"


      override def beforeStarted() = {
        process = Option(
          Process(npmBuildDev, base).run()
        )
      }

      override def afterStarted() = {
        process = Option(
          Process(npmRunDev, base).run()
        )
      }

      override def afterStopped() = {
        process.foreach(_.destroy())
        process = None
      }
    }

    WebpackHook
  }
}