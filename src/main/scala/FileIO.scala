import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter


// REVISAR Y CONSULTAR LA IMPLEMENTACION
// object TextProcessing {
//   def formatDateFromUTC(utc: Long): String = {
//     // 1. Convertimos los segundos a un objeto Instant
//     val instant = Instant.ofEpochSecond(utc)
    
//     // 2. Definimos el formato (ej: Día/Mes/Año Hora:Min)
//     val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
//       .withZone(ZoneId.systemDefault()) // Esto lo pasa a la hora de tu PC (Córdoba)

//     formatter.format(instant)
//   }
// }

object FileIO {

  implicit val formats: Formats = DefaultFormats

  type Subscription = (String, String) // (subredditName, url)

  type Post = (String, String, String, String) // (subreddit, title, selftext, created_utc)

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
  def downloadFeed(url: String): List[Post] = {
    // Guardamos en source el contenido de url
    val source = Source.fromURL(url)
    // Guardamos el contenido en un string
    val content = source.mkString
    // Cerramos el fd
    source.close()

    val json = parse(content)

    // Creamos lista de los posts
    val data = (json \ "data" \ "children").children

    data.map{item =>
      // se extrae un string con el valor del campo subreddit
      val subreddit = (item \ "data" \ "subreddit").extract[String]
      // se extrae un string con el valor del campo title
      val title = (item \ "data" \ "title").extract[String]
      // se extrae un string con el valor del campo selftext
      val selftext = (item \ "data" \ "selftext").extract[String]
      // se extrae el valor del campo created_utc
      val created_utc = (item \ "data" \ "created_utc").extract[Double].toLong
      // formateamos la fecha (Día/Mes/Año Hora:Min)
      val date = TextProcessing.formatDateFromUTC(created_utc)
      // retorno de tupla (funcion anonima)
      (subreddit, title, selftext, date)
    }
  }
}
