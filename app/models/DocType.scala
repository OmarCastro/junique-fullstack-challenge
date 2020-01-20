package models

import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory
import play.api.libs.json.{JsObject, Json, OWrites}

import scala.collection.immutable.Map

sealed abstract class DocType{
  val fullName : String
}
case class Html5(versionNumber: Double, fullName: String, valid: Boolean = true) extends DocType
case class Html4(versionNumber: Double, fullName: String, valid: Boolean = true) extends DocType
case class XHtml1(versionNumber: Double, fullName: String, valid: Boolean = true) extends DocType
case class UnknownDocType(fullName: String = "unknown Doctype", valid: Boolean = false) extends DocType


object Html5 {  implicit val jsonWrites: OWrites[Html5] = Json.writes[Html5] }
object Html4 {  implicit val jsonWrites: OWrites[Html4] = Json.writes[Html4] }
object XHtml1 {  implicit val jsonWrites: OWrites[XHtml1] = Json.writes[XHtml1] }
object UnknownDocType {  implicit val jsonWrites: OWrites[UnknownDocType] = Json.writes[UnknownDocType] }

object DocTypeWriter extends OWrites[DocType]{
  override def writes(docType: DocType): JsObject = {
    docType match {
      case html5: Html5 => Html5.jsonWrites.writes(html5)
      case html4: Html4 => Html4.jsonWrites.writes(html4)
      case xHtml1: XHtml1 => XHtml1.jsonWrites.writes(xHtml1)
      case unknown: UnknownDocType => UnknownDocType.jsonWrites.writes(unknown)
    }
  }
}

object DocType {
  implicit val docTypeWriter: OWrites[DocType] = DocTypeWriter

  val HtmlVersions: Map[String, DocType] = Map(
    "<!DOCTYPE html>".toLowerCase -> Html5(5, "HTML 5"),
    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">".toLowerCase -> Html4(4.01, "HTML 4.01 Strict"),
    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">".toLowerCase -> Html4(4.01, "HTML 4.01 Transitional"),
    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">".toLowerCase -> Html4(4.01, "HTML 4.01 FrameSet"),
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">".toLowerCase -> XHtml1(1.0, "XHTML 1.0 Strict"),
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">".toLowerCase -> XHtml1(1.0, "XHTML 1.0 Transitional"),
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">".toLowerCase -> XHtml1(1.0, "XHTML 1.0 FrameSet"),
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">".toLowerCase -> XHtml1(1.1, "XHTML 1.1"),
  )


  def getFromTopComment(topComment: String): DocType ={
    HtmlVersions.get(topComment.toLowerCase) match {
      case Some(value) => value
      case None => UnknownDocType()
    }
  }



}