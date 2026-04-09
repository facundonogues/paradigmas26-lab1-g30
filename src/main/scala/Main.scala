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
        val results = subscriptions.map { case (name, url) =>
          val fetchMsg = s"Fetching posts from: $url\n"

          val result = FileIO.downloadFeed(url) match {
            case Some(posts) =>
              val filtered = Formatters.filterPosts(posts)
              val formatted = Formatters.formatSubscription(url, filtered)
              fetchMsg + formatted

            case None =>
              fetchMsg + s"Error descargando: $url\n"
          }

          result
        }

        val output = results.mkString("\n")
        println(output)
    }
  }
}