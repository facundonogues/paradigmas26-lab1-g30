package fileio // Empaqueto para usar en formatters el tipo Post

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import scala.util.Using
import scala.util.Try


object TextProcessing {
  def formatDateFromUTC(utc: Long, zone: ZoneId): Option[String] = Try {
    // 1. Convertimos los segundos a un objeto Instant
    val instant = Instant.ofEpochSecond(utc)
    
    // 2. Definimos el formato (ej: Día/Mes/Año Hora:Min)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
      .withZone(zone)

    formatter.format(instant)
  }.toOption
}

object FileIO {

  implicit val formats: Formats = DefaultFormats

  type Subscription = (String, String) // (subredditName, url)

  type Post = (String, String, String, String, Int, String) // (subreddit, title, selftext, created_utc, score, url)

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
            val url  = (item \ "data" \ "url").extract[String]
            val created   = (item \ "data" \ "created_utc").extract[Double].toLong

            val zone = ZoneId.systemDefault() // zona horaria local
            val date = TextProcessing.formatDateFromUTC(created, zone).getOrElse("Fecha no disponible")

            val score  = (item \ "data" \ "score").extract[Int]

            (subreddit, title, selftext, date, score, url)
          }.toOption
        }
      }
    }
  }
}
