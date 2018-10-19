package han.ica.app
import org.json4s.native.JsonMethods._
import org.jsoup.Connection.Response

import scala.collection.JavaConverters._

object Coop {

  def extractProductInformation(response: Response): List[String] = {
    response.parse().getElementsByTag("article").asScala.map(e => e.attr("data-product")).toList
  }

  def printProducts(list: List[String]): Unit = {
    for (item <- list) {
      println("Product naam is " + compact(render(parse(item) \ "name")) + " met de bijbehorende prijs " + compact(render(parse(item) \ "price")))
    }
  }
}

