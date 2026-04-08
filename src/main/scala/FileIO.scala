package fileio // Empaqueto para usar en formatters el tipo Post

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import scala.util.Using
import scala.util.Try


object TextProcessing {
  def formatDateFromUTC(utc: Long): String = {
    // 1. Convertimos los segundos a un objeto Instant
    val instant = Instant.ofEpochSecond(utc)
    
    // 2. Definimos el formato (ej: Día/Mes/Año Hora:Min)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
      .withZone(ZoneId.systemDefault()) // Esto lo pasa a la hora de tu PC (Córdoba)

    formatter.format(instant)
  }
}

object FileIO {

  implicit val formats: Formats = DefaultFormats

  type Subscription = (String, String) // (subredditName, url)

  type Post = (String, String, String, String) // (subreddit, title, selftext, created_utc)

  def readSubscriptions(path: String): Option[List[Subscription]] = {
    val contentOpt =
      Using(Source.fromFile(path))(_.mkString).toOption

    contentOpt.flatMap { content =>
      val jsonOpt = Try(parse(content)).toOption

      jsonOpt.map { json =>
        json.children.flatMap { item =>
          Try {
            val name = (item \ "name").extract[String]
            val url  = (item \ "url").extract[String]
            (name, url)
          }.toOption
        }
      }
    }
  }

  def downloadFeed(url: String): Option[List[Post]] = {
    val downloadOpt = Using(Source.fromURL(url))(_.mkString).toOption

    downloadOpt.flatMap { content =>
      val jsonOpt = Try(parse(content)).toOption
      
      jsonOpt.map { json =>
        val data = (json \ "data" \ "children").children

        data.flatMap { item =>
          Try {
            val subreddit = (item \ "data" \ "subreddit").extract[String]
            val title     = (item \ "data" \ "title").extract[String]
            val selftext  = (item \ "data" \ "selftext").extract[String]
            val created   = (item \ "data" \ "created_utc").extract[Double].toLong

            val date = TextProcessing.formatDateFromUTC(created)

            (subreddit, title, selftext, date)
          }.toOption
        }
      }
    }
  }
}
