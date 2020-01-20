import java.net.InetSocketAddress

import play.sbt.PlayRunHook
import sbt._

import scala.sys.process.Process

object NpmWebpack {
  def apply(base: File): PlayRunHook = {
    object WebpackHook extends PlayRunHook {
      var process: Option[Process] = None

      val shell: Seq[String] = if (sys.props("os.name").contains("Windows")) Seq("cmd", "/c") else Seq("bash", "-c")
      val npmCi: Seq[String] = shell :+ "npm ci"
      val npmBuildDev: Seq[String] = shell :+   "npm run build:dev"
      val npmRunDev: Seq[String] = shell :+   "npm run watch:dev"


      override def beforeStarted(): Unit = {
        val npmCiExitCode = Process(npmCi, base).run().exitValue
        if(npmCiExitCode != 0){
          throw new RuntimeException("Error running npm ci")
        }
        process = Option(
          Process(npmBuildDev, base).run()
        )
      }

      override def afterStarted(): Unit = {
        process = Option(
          Process(npmRunDev, base).run()
        )
      }

      override def afterStopped(): Unit = {
        process.foreach(_.destroy())
        process = None
      }
    }

    WebpackHook
  }
}