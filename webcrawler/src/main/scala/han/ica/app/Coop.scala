package han.ica.app

import org.json4s.native.JsonMethods
import org.jsoup.Connection.Response

import scala.collection.JavaConverters._

object Coop {

  def extractProductInformation(response: Response): List[String] = {
   var res =  response.parse().getElementsByTag("article").asScala.map(e => e.attr("data-product")).toList
    println(res.head)
    val json = JsonMethods.parse(res.head)
    println("name is " + (json \ "name"))
    return res
  }
}