package han.ica.app

import org.json4s.JsonAST.JDouble
import org.json4s.native.JsonMethods._
import org.json4s.native.JsonMethods.parse
import org.jsoup.Connection.Response

import scala.collection.JavaConverters._
import scala.collection.mutable

object Jumbo {

  def extractProductInformation(response: Response): List[String] = {
    var productInfo = response.parse().getElementsByTag("div").asScala
      .map { e => e.attr("data-jum-product-impression") }
    var productPrice = response.parse().getElementsByTag("input").asScala
      .map { e => e.attr("jum-data-price") }
    productInfo ++= productPrice
    val list = filterArray(productInfo)
    list.toList
  }

  def filterArray(array: mutable.Buffer[String]): mutable.Buffer[String] = {
    var newArray = array.filter(_.length > 2)
    convertToJson(newArray)
    newArray
  }

  def convertToJson(array: mutable.Buffer[String]): Unit = {
    for (item <- array) {
      if (isANumber(item)) {
        val double: Double = item.toDouble
        var json = parse("""{ "price": +""" + double + "}")
        array(array.indexOf(item)) = json.toString
        JDouble.apply(item.toDouble)
      }
    }
    array.grouped(array.length / 2).toList
  }

  def isANumber(x: String): Boolean = x.matches("^-?[0-9]+(\\.[0-9]+)?$")

  def printProducts(list: List[String]): Unit = {
    for (i <- 0 until list.length / 2) {
      val price = ("""\d+""".r findAllIn list(list.length / 2 + i)).toList
      println("Product naam is " + compact(render(parse(list(i)) \ "name")) + " met de bijbehorende prijs " + price.head + "." + price.last)
    }
  }
}
