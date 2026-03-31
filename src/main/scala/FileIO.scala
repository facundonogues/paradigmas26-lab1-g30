import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

object FileIO {

  implicit val formats: Formats = DefaultFormats

  type Subscription = (String, String) // (subredditName, url)

  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(path: String): List[Subscription] = {
    // List(
    //   "https://www.reddit.com/r/scala/.json?count=10",
    //   "https://www.reddit.com/r/learnprogramming/.json?count=10"
    // )
    val fd = Source.fromFile(path)  // File descriptor al path
    val str = fd.mkString           // guarda el valor del fd
    fd.close()                      // cerramos fd

    val json = parse(str)

    json.children.map{item =>
      // se extrae un string con el valor del campo name
      val name = (item \ "name").extract[String]
      // se extrae un string con el valor del campo url
      val url = (item \ "url").extract[String]
      // retorno de tupla (funcion anonima)
      (name, url)
    }

  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    source.mkString
  }
}
