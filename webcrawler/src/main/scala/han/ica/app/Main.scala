package han.ica.app

import scala.collection.JavaConverters._
import org.jsoup.Connection.Response
import org.jsoup.Jsoup

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


object Main {

  def main(args: Array[String]): Unit = {
    var url = "http://google.nl"

    crawler(url)
  }



  var checkedUrls = ListBuffer[String]()
  var nonCheckedUrls = ListBuffer[String]()
  var hashMap = mutable.HashMap.empty[String, String]

  def crawler(url: String): mutable.HashMap[String, String] = {
    var currentUrl: String = url
    if (!checkedUrls.contains(currentUrl)) {
      nonCheckedUrls ++= getAllUrlsFromLink(get(currentUrl))
      for (ncu <- nonCheckedUrls) {
        if (isValidUrl(ncu)) {
          println("ncu "+ ncu)
          if (checkedUrls.contains(ncu)) {
            nonCheckedUrls -= ncu
            hashMap += "k " -> ncu
          }
          else {
            currentUrl = ncu
          }
        }
        else {
          nonCheckedUrls -= ncu
        }
      }
    }
    if (!checkedUrls.contains(currentUrl)) {
      checkedUrls += currentUrl
    }
    crawler(currentUrl)
    return hashMap
  }

  /** Will return false on the following urls:
    * www.foufos, http://www.foufos, http://foufos, www.mp3#.com, www.foufos-.gr, www.-foufos.gr
    */
  def isValidUrl(url: String): Boolean = {
    var regex = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})"
    if (url.matches(regex)) {
      return true
    }
    false
  }

  def get(url: String): Response = {
    Jsoup.connect(url).ignoreContentType(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()
  }

  def getAllUrlsFromLink(response: Response): List[String] = {
    if (response.contentType().startsWith("text/html")) {
      val links: List[String] = response.parse().getElementsByTag("a").asScala.map(e => e.attr("href")).toList
      return links
    }
    null
  }
}
