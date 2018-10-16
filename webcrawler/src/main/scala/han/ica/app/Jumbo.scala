package han.ica.app

import org.jsoup.Connection.Response

import scala.collection.JavaConverters._

object Jumbo {

  def extractProductInformation(response: Response): Unit = {
    var productInfo = response.parse().getElementsByTag("div").asScala
      .map{e => e.attr("data-jum-product-impression")}.toList

//
//    for(product <- productInfo) {
//      var json : JValue= product.insta
//      (json \ "name").extract[String]
//      println(product)
//      println(product.extract)
//    }
  }
}



  ///-> e.attr("jum-price-format")}.toList)
