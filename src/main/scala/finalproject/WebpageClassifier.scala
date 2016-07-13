package finalproject
import org.jsoup

//import scala.scalajs.js
import scala.scalajs.js.JSApp

/**
  * Created by malarconba001 on 7/10/2016.
  */
object WebpageClassifier extends JSApp {

  def main: Unit = {

    DocumentClassifier.appendDocumentCategory("houses", "the housing market is good")
    DocumentClassifier.appendDocumentCategory("houses", "house repairs are time consuming")
    DocumentClassifier.appendDocumentCategory("cars","I have a green car")
    DocumentClassifier.appendDocumentCategory("cars","my car needs an oil change")
    println(DocumentClassifier.classifyDocument("green house"))
//    println("USAGE: \n\tWebpageClassifier.addWebpageCategory(category,url)\n\tWebpageClassifier.classify(url)" )
  }

  def addWebpageCategory(category:String, url:String): Unit = {
    DocumentClassifier.appendDocumentCategory(category,
      jsoup.Jsoup.connect(url).get.body.text
    )
  }

  def classify(url:String): Unit = {
    DocumentClassifier.classifyDocument(
      jsoup.Jsoup.connect(url).get.body.text
    )
  }

}
