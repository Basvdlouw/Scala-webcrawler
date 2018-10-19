package han.ica.app

import org.json4s._
import org.json4s.native.JsonMethods._
import org.jsoup.Connection.Response
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer


object Main {

  def main(args: Array[String]): Unit = {


    val url = "https://www.coop.nl/INTERSHOP/web/WFS/COOP-COOPBase-Site/nl_NL/-/EUR/ViewStandardCatalog-Browse?CatalogCategoryID=&PageSize=24&SortingAttribute=&CategoryName=21&CatalogID=COOP&SearchParameter=%26%40QueryTerm%3D*%26ContextCategoryUUID%3D548KAQCN5XAAAAFWkvJMRzCy%26OnlineFlag%3D1%26SupplierName%3DHertog%2BJan%26%40Sort.SalesRanking%3D0"
    val url2 = "https://www.jumbo.com/zoeken?SearchTerm=Hertog+jan&search="
    val url3 = "https://www.coop.nl/zoeken?SearchTerm=hertog%2520jan"
    // println(get(url).parse().html())
    //get(url2)
    // Jumbo.extractProductInformation(get(url2))
    compareProductInformation(Coop.extractProductInformation(get(url3)), Jumbo.extractProductInformation(get(url2)))
    //    crawler(url)
    //    print(checkedUrls)
    //    print(nonCheckedUrls)

  }


  //Experimenting with functions as parameters
  def compareProductInformation(coop: List[String], jumbo: List[String]): Unit = {
//    println("Bij de COOP zijn bij de ingevoerde URL de volgende producten gevonden:")
//    for (item <- coop) {
//      val json = parse(item)
//      println("Product naam is " + compact(render(json \ "name")) + " met de bijbehorende prijs " + compact(render(json \ "price")))
//    }

    println("Bij de Jumbo zijn bij de ingevoerde URL de volgende producten gevonden:")
    for (i <- 0 to jumbo.length / 2) {
      val json = parse(jumbo(i))
      val json2 = jumbo(jumbo.length / 2 + i)
      println("Product naam is " + compact(render(json \ "name")) + " met de bijbehorende prijs " + json2)
    }
  }


  var checkedUrls = ListBuffer[String]()
  var nonCheckedUrls = ListBuffer[String]()


  //this function should later be refactored
  //currently not used but should be used for the end product: get product information with only the index url e.g: https://www.ah.nl
  def crawler(url: String): Unit = {
    var currentUrl: String = url
    if (!checkedUrls.contains(currentUrl)) {
      var b = false
      var i = 0
      nonCheckedUrls ++= getAllUrlsFromLink(get(currentUrl))
      checkedUrls += currentUrl
      while (!b && i < nonCheckedUrls.size) {
        if (isValidUrl(nonCheckedUrls(i))) {
          if (checkedUrls.contains(nonCheckedUrls(i))) {
            nonCheckedUrls -= nonCheckedUrls(i)
          }
          else {
            currentUrl = nonCheckedUrls(i)
            b = true
          }
        }
        else {
          nonCheckedUrls -= nonCheckedUrls(i)
          i += 1
        }
      }
    }
    else {
      var b = false
      var i = 0
      while (!b) {
        if (!checkedUrls.contains(nonCheckedUrls(i)) && isValidUrl(nonCheckedUrls(i))) {
          currentUrl = nonCheckedUrls(i)
          b = true
        }
        i += 1
      }
    }
    if (!currentUrl.isEmpty && isValidUrl(currentUrl)) {
      crawler(currentUrl)
    }
  }

  /** Will return false on the following urls:
    * www.foufos, http://www.foufos, http://foufos, www.mp3#.com, www.foufos-.gr, www.-foufos.gr
    */
  def isValidUrl(url: String): Boolean = {
    val regex = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})"
    if (url.matches(regex)) {
      return true
    }
    false
  }

  def get(url: String): Response = {
    var response = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()
    if (response.statusCode() != 200) {
      println("received error code :" + response.statusCode())
    }
    response
  }

  def getAllUrlsFromLink(response: Response): List[String] = {
    if (response.contentType().startsWith("text/html") && response.statusCode() == 200) {
      return response.parse().getElementsByTag("a").asScala.map(e => e.attr("href")).toList
    }
    null
  }
}
