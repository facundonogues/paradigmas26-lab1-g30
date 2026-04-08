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
              val palabraMasRepetida = Formatters.countWords(posts).maxBy { case (_, value) => value }
              println(s"""Palabra mas repetida: ${palabraMasRepetida._1}, Ocurrencias: ${palabraMasRepetida._2}""")
              val filtered = Formatters.filterPosts(posts)
              println(s"""Primeros 5 posts:""")
              val firstPosts = filtered.take(5)
              println(Formatters.formatSubscription(url, firstPosts))
              Some(posts)
            case None =>
              println(s"Error descargando: $url")
              None
          }
        }
    }
  }
}