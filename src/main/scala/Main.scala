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
          println(s"Fetching posts from: $url")

          FileIO.downloadFeed(url) match {
            case Some(posts) =>
              Some((url, posts))
            case None =>
              println(s"Error descargando: $url")
              None
          }
        }

        val output = allPosts
          .map { case (url, posts) =>
            val filtered = Formatters.filterPosts(posts)
            Formatters.formatSubscription(url, filtered)
          }
          .mkString("\n")

        println(output)
    }
  }
}