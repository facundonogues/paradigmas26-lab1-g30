import fileio.FileIO.Post // importo para tener el tipo Post

object Formatters {

  def filterPosts(posts: List[Post]): List[Post] = {
    posts.filter {case (_, title, selftext, _) =>
      // Filtramos los posts que no tienen title ni selftext vacío
      (title.trim != "") || (selftext.trim != "") 
    }
  }

  def formatSubscription(url: String, posts: List[Post]): String = {
    val header =
      s"\n${"=" * 80}\nPosts from: $url\n${"=" * 80}"

    val formattedPosts = posts.map {
      case (subreddit, title, selftext, date) =>
        s"""[$date] r/$subreddit
            |Title: $title
            |$selftext
            |${"-" * 40}""".stripMargin
    }

    //le añadi un barra n mas para que haya mas espacio entre cada post
    header + "\n" + formattedPosts.mkString("\n\n")
  }
}
