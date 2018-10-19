package han.ica.app

import org.jsoup.Connection.Response

import scala.collection.JavaConverters._

object Coop {

  def extractProductInformation(response: Response): List[String] = {
     response.parse().getElementsByTag("article").asScala.map(e => e.attr("data-product")).toList
  }
}

