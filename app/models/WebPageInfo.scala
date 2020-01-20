package models

import play.api.libs.json.{JsObject, JsString, Json, OWrites}


sealed abstract class WebPageInfo
case class HTMLPageInfo(
                         url: String,
                         docType: DocType,
                         title: String,
                         h1Headings: Int,
                         h2Headings: Int,
                         h3Headings: Int,
                         h4Headings: Int,
                         h5Headings: Int,
                         h6Headings: Int,
                         internalLinks: Int,
                         externalLinks: Int,
                         inaccessibleLinks: Int,
                         hasLoginForm: Boolean,
                         htmlContent: String
                       ) extends WebPageInfo

case class InvalidPage(reason: String, url: String) extends WebPageInfo
case class ErrorPageInfo(statusCode: Int, url: String) extends WebPageInfo
case class UnreachablePage(reason: String, url: String) extends WebPageInfo


object HTMLPageInfo {
  implicit val jsonWrites: OWrites[HTMLPageInfo] = Json.writes[HTMLPageInfo].transform(_.as[JsObject] + ("status" -> JsString("ok")))
}
object InvalidPage {
  implicit val jsonWrites: OWrites[InvalidPage] = Json.writes[InvalidPage].transform(_.as[JsObject] + ("status" -> JsString("invalid")))
}
object ErrorPageInfo {
  implicit val jsonWrites: OWrites[ErrorPageInfo] = Json.writes[ErrorPageInfo].transform(_.as[JsObject] + ("status" -> JsString("error")))
}
object UnreachablePage {
  implicit val jsonWrites: OWrites[UnreachablePage] = Json.writes[UnreachablePage].transform(_.as[JsObject] + ("status" -> JsString("unreachable")))
}


object WebPageInfoJsonWrites extends OWrites[WebPageInfo]{
  override def writes(docType: WebPageInfo): JsObject = {
    docType match {
      case htmlPageInfo: HTMLPageInfo => HTMLPageInfo.jsonWrites.writes(htmlPageInfo)
      case invalidPage: InvalidPage => InvalidPage.jsonWrites.writes(invalidPage)
      case unreachablePageInfo: ErrorPageInfo => ErrorPageInfo.jsonWrites.writes(unreachablePageInfo)
      case unreachablePageInfo: UnreachablePage => UnreachablePage.jsonWrites.writes(unreachablePageInfo)
    }
  }
}

object WebPageInfo {
  implicit val jsonWrites: OWrites[WebPageInfo] = WebPageInfoJsonWrites
}
