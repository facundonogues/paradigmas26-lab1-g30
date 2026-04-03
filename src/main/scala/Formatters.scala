import fileio.FileIO.Post // importo para tener el tipo Post

object Formatters {

  def filterPosts(posts: List[Post]): List[Post] = {
    posts.filter {case (_, title, selftext, _) =>
      (title.trim != "") || (selftext.trim != "") // Filtramos los posts que no tienen title ni selftext vacío
    }
  }

  // Pure function to format posts from a subscription
  def formatSubscription(url: String, posts: String): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"
    val formattedPosts = posts.take(80)
    header + "\n" + formattedPosts
  }
}
