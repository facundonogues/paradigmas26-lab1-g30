import fileio.FileIO

object Main {
  def main(args: Array[String]): Unit = {
    val header = s"${"=" * 80}\nInforme: Curador Funcional de Feeds de Reddit\n${"=" * 80}\n"

    val maybeSubs = FileIO.readSubscriptions("subscriptions.json")

    val output = maybeSubs match {
      case None =>
        "Error leyendo subscriptions.json"

      case Some(subscriptions) =>
        val results = subscriptions.map { case (name, url) =>

        FileIO.downloadFeed(url) match {
          case Some(posts) =>
            val subsScore = Formatters.scoring(posts)
            val palabraMasRepetida =
              Formatters.countWords(posts).maxBy { case (_, value) => value }

            val filtered = Formatters.filterPosts(posts)
            val firstPosts = filtered.take(5)

            val finalFormat =
              Formatters.formatSubscription(name, subsScore, palabraMasRepetida, firstPosts)

            finalFormat

          case None =>
            s"Error descargando: $url\n"
          }
        }

        header + results.mkString("\n")
    }
    //un solo print para mantener declaratividad al maximo
    //CORRER sbt --error run > informe.txt para ver lindo el informe
    println(output)
  }
}