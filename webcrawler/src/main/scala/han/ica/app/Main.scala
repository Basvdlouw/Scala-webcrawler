package han.ica.app

import org.jsoup.Connection.Response
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer


object Main {
  //COOP often returns a server error (500) try again until it works.
  val url1 = "https://www.coop.nl/zoeken?SearchTerm=hertog%2520jan"
  val url2 = "https://www.jumbo.com/zoeken?SearchTerm=Hertog+jan&search="
  val url3 = "https://www.coop.nl/boodschappen/dranken/bier" //Another link (coop) for test purposes (tested succesfully)
  val url4 = "https://www.jumbo.com/zoeken?SearchTerm=bier+&search=" //Another link (jumbo) for test purposes (tested succesfully)

  def main(args: Array[String]): Unit = {
    compareProductInformation(Coop.extractProductInformation(get(url1)), Jumbo.extractProductInformation(get(url2)))
  }

  //Experimenting with functions as parameters
  def compareProductInformation(shop1: List[String], shop2: List[String]): Unit = {
    println("Bij de COOP zijn bij de ingevoerde URL de volgende producten gevonden:")
    Coop.printProducts(shop1)
    println("Bij de Jumbo zijn bij de ingevoerde URL de volgende producten gevonden:")
    Jumbo.printProducts(shop2)
  }


  //Variables used by the crawler function
  //Crawler function not yet finished, currently collects a bunch off urls until it crashes (function doesn't handle error codes yet)
  var checkedUrls: ListBuffer[String] = ListBuffer[String]()
  var nonCheckedUrls: ListBuffer[String] = ListBuffer[String]()


  //this function should later be refactored
  //currently not used but should be used for the end product: get product information with only the index url e.g: https://www.coop.nl
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
