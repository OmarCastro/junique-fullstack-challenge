package controllers

import javax.inject._
import play.api._
import play.api.http.MimeTypes
import play.api.mvc._
import play.api.routing._
import play.api.libs.json.Json
import services.WebPageService

import scala.concurrent.ExecutionContext
/**
 * Application's home page.
 */
@Singleton
class HomeController @Inject()(webpage: WebPageService, cc: ControllerComponents)(implicit val executionContext: ExecutionContext) extends AbstractController(cc) {


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def verifyWebPage(page: String) = Action.async { implicit request: Request[AnyContent] =>

    webpage.getInfoFromPage(page)
      .map(Json.toJson(_))
        .map(Ok(_))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("playRoutes")(
        routes.javascript.HomeController.index,
        routes.javascript.HomeController.verifyWebPage
      )
    ).as(MimeTypes.JAVASCRIPT)
  }

  
}
