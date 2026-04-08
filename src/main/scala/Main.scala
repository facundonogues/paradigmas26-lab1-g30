import fileio.FileIO // Importo para poder tener Post

object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"
    println(header)

    val maybeSubs = FileIO.readSubscriptions("subscriptions.json")

    maybeSubs match {
      case None =>
        println("Error leyendo subscriptions.json")

      case Some(subscriptions) =>
        val allPosts = subscriptions.flatMap { case (name, url) =>
          FileIO.downloadFeed(url) match {
            case Some(posts) =>
              val subsScore = Formatters.scoring(posts)
              println(s"""Score de la suscripcion ${name}: ${subsScore}""")
              Some(posts)
            case None =>
              println(s"Error descargando: $url")
              None
          }
        }

        // val output = allPosts
        //   .map { case (url, posts) =>
        //     val filtered = Formatters.filterPosts(posts)
        //     Formatters.formatSubscription(url, filtered)
        //   }
        //   .mkString("\n")

        // println(output)

    }

    // Nombre y suma total de scores de cada Subscription LISTO
    // Palabras más frecuentes con sus ocurrencias, tal como las extrajeron en el ejercicio 5
    // Cinco primeros posts con su título, fecha y URL

  }
}