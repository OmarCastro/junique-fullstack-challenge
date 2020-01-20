package services

import java.net.{MalformedURLException, URI, URL, UnknownHostException}

import javax.inject.{Inject, Singleton}
import models.{InvalidPage, _}
import org.jsoup.{HttpStatusException, Jsoup}
import org.jsoup.nodes.{Document, DocumentType, Element}
import play.api.Logger
import play.api.libs.ws.{WSClient, WSRequest}

import scala.collection.mutable
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._;
@Singleton
class WebPageService @Inject() (ws: WSClient)(implicit val executionContext: ExecutionContext) {
  private val logger = Logger("play")



  def getInfoFromPage(urlString: String): Future[WebPageInfo] = {
    try {
      var urlToSearch = urlString
      //throw MalformedURLException early on invalid url
      try {
        new URL(urlToSearch);
      } catch {
        case ex: MalformedURLException => if(ex.getMessage.startsWith("no protocol")){
          urlToSearch = "http://"+urlToSearch
        } else throw ex;
      }
      getInfoFromDocument(Jsoup.connect(urlToSearch).get())
    } catch {
      case ex: MalformedURLException  => Future(InvalidPage(ex.getMessage, urlString))
      case ex: UnknownHostException  => Future(UnreachablePage("unreachable host: " + ex.getMessage, urlString))
      case ex: HttpStatusException  => Future(ErrorPageInfo(ex.getStatusCode, urlString))
      case ex: Exception => Future(InvalidPage(ex.getMessage, urlString))
    }
  }

  def getInfoFromDocument(doc : Document): Future[WebPageInfo] ={
    val baseUrl: URL = new URL(doc.ownerDocument().location());
    val docType: DocType = doc.childNodes().stream()
      .filter(_.isInstanceOf[DocumentType])
      .findFirst()
      .map(_.outerHtml)
      .map(DocType.getFromTopComment(_))
      .orElse(UnknownDocType())

    val title : String = doc.title();

    val internalLinks = mutable.Buffer[Element]()
    val externalLinks = mutable.Buffer[Element]()

    val links = doc.select("a[href]")
    for (link: Element <- links.asScala){
      val href = link.attr("href");
      val uri = new URI(href)
      if(uri.isAbsolute && !uri.getHost.equals(baseUrl.getHost)){
        externalLinks.append(link);
      } else {
        internalLinks.addOne(link);
      }
    }

    getInaccessibleLinks(doc).map(inaccessibleLinks => {
      HTMLPageInfo(
        url = doc.location(),
        docType = docType,
        title = title,
        h1Headings = doc.select("h1").size,
        h2Headings = doc.select("h2").size,
        h3Headings = doc.select("h3").size,
        h4Headings = doc.select("h4").size,
        h5Headings = doc.select("h5").size,
        h6Headings = doc.select("h6").size,
        internalLinks = internalLinks.size,
        externalLinks = externalLinks.size,
        inaccessibleLinks = inaccessibleLinks.size,
        hasLoginForm = hasForm(doc),
        htmlContent = doc.outerHtml()
      )
    })
  }

  /**
   * Gets a list of inaccessible link elements
   */
  private def getInaccessibleLinks(doc : Document): Future[Seq[Element]] = {
    val links = doc.select("a[href]")

    val visitedLinks = mutable.HashMap[String, Future[Boolean]]()
    for (link: Element <- links.asScala) {
      val absoluteUrl = linkAbsoluteUrl(link);
      visitedLinks.getOrElseUpdate(absoluteUrl, isUrlAccessible(absoluteUrl));
    }

    Future.sequence(visitedLinks.values)
      .map(_ => visitedLinks.toMap.transform((_, value) => value.value.exists(_.getOrElse(false))))
      .map(visitedLinksResult => {
      val inaccessibleLinks = mutable.Buffer[Element]()

      for (link: Element <- links.asScala) {
        val absoluteUrl = linkAbsoluteUrl(link);
        val isAccessible = visitedLinksResult.getOrElse(absoluteUrl, false)
        if (!isAccessible) {
          inaccessibleLinks.append(link)
        }
      }

      inaccessibleLinks.toSeq
    })
  }

  /**
   * gets href absolute url from base url
   */
  private def linkAbsoluteUrl(link: Element) : String = {
    val baseUrl: URL = new URL(link.ownerDocument().location());
    val href = link.attr("href");
    val uri = new URI(href)
    if (uri.isAbsolute) {
      href
    } else {
      new URL(baseUrl, href).toString;
    }
  }


  /**
   *  Checks if document has a form
   *  The assumptions to define a form are as follows:
   *    1. a username field exists in the document
   *    2. a password field exists in the document
   *  Since a form can be created in different ways, be it with divs, form, or a custom element,
   *  the existence of those 2 field is a good enough hint of a login form
   */
  private def hasForm(doc : Document): Boolean ={
    val hasPasswordField = doc.select("input[type=\"password\"]").size > 0
    val hasUserField = doc.select("input[name]").stream()
      .anyMatch(element => {
        "user".equalsIgnoreCase(element.attr("name")) ||
        "email".equalsIgnoreCase(element.attr("name")) ||
        "username".equalsIgnoreCase(element.attr("name"))
      })

    hasUserField && hasPasswordField
  }

  /**
   * Checks if the URL string is accessible, returns true only when
   * receiving a successful response (2XX status code)
   */
  private def isUrlAccessible(urlString: String): Future[Boolean] ={
    logger.info(s"checking accessibility of link ${urlString}");
    val request: WSRequest = ws.url(urlString)
    request.get()
      .map(response => {
        logger.info(s"status code of ${urlString} ${response.status}")
        response.status < 300 && response.status >= 200
      })
      .recover(_ => {
        logger.info(s"fail on ${urlString}")
        false
      })

  }
}
