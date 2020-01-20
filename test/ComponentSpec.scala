import models.{ErrorPageInfo, HTMLPageInfo, InvalidPage, WebPageInfo}
import org.scalatestplus.play._
import play.api.Mode
import play.api.test.Helpers.HTML
import play.api.test.WsTestClient
import services.WebPageService
import play.api.mvc._
import play.api.routing.sird._
import play.core.server.{Server, ServerConfig}

import scala.concurrent.Await
import scala.concurrent.duration._
/**
 * Unit tests can run without a full Play application.
 */
class ComponentSpec extends PlaySpec  {

  import scala.concurrent.ExecutionContext.Implicits.global

  "WebPageService.getInfoFromPage" should {

    "return a valid htmlPageInfo on success" in {
      Server.withRouterFromComponents(ServerConfig(port = Some(9001), mode = Mode.Test)) { components =>
        import Results._
        import components.{defaultActionBuilder => Action}
        {
          case GET(p"/lorem-ipsum") =>
            Action {
              Ok("""
                   |<!DOCTYPE html>
                   |<html lang="en">
                   |<head>
                   |    <title>Lorem ipsum</title>
                   |    <meta charset="UTF-8">
                   |    <title>Lorem ipsum</title>
                   |</head>
                   |<body>
                   |    <h1>HTML Ipsum Presents</h1>
                   |    <p><strong>Pellentesque habitant morbi tristique</strong> senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. <em>Aenean ultricies mi vitae est.</em> Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, <code>commodo vitae</code>, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. <a href="#">Donec non enim</a> in turpis pulvinar facilisis. Ut felis.</p>
                   |    <h2>Header Level 2</h2>
                   |    <ol>
                   |        <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>
                   |        <li>Aliquam tincidunt mauris eu risus.</li>
                   |    </ol>
                   |    <blockquote><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus magna. Cras in mi at felis aliquet congue. Ut a est eget ligula molestie gravida. Curabitur massa. Donec eleifend, libero at sagittis mollis, tellus est malesuada tellus, at luctus turpis elit sit amet quam. Vivamus pretium ornare est.</p></blockquote>
                   |    <h3>Header Level 3</h3>
                   |    <ul>
                   |        <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>
                   |        <li>Aliquam tincidunt mauris eu risus.</li>
                   |    </ul>
                   |</body>
                   |</html>
            """.stripMargin).as(HTML)
            }
        }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result: WebPageInfo = Await.result(new WebPageService(client).getInfoFromPage("http://localhost:9001/lorem-ipsum"), 10.seconds)
          val htmlPageInfo = result.asInstanceOf[HTMLPageInfo]
          htmlPageInfo.docType.fullName mustBe "HTML 5"
          htmlPageInfo.title mustBe "Lorem ipsum"
          htmlPageInfo.h1Headings mustBe 1
        }
      }
    }
  }

  "return a invalid page on url error" in {
    WsTestClient.withClient { client =>
      val result: WebPageInfo = Await.result(new WebPageService(client).getInfoFromPage("invalid://url:3001"), 10.seconds)
      val invalidPage = result.asInstanceOf[InvalidPage]
      invalidPage.reason mustBe "unknown protocol: invalid"
    }
  }

  "return a error page on 500" in {
    WsTestClient.withClient { client =>
      val result: WebPageInfo = Await.result(new WebPageService(client).getInfoFromPage("http://httpstat.us/500"), 10.seconds)
      val errorPage = result.asInstanceOf[ErrorPageInfo]
      errorPage.statusCode mustBe 500
    }
  }

}