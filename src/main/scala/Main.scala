import fileio.FileIO // Importo para poder tener Post

object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"

    val subscriptions: List[FileIO.Subscription] = FileIO.readSubscriptions("subscriptions.json")

    val allPosts: List[(String, List[FileIO.Post])] = subscriptions.map { case (name, url) =>
      println(s"Fetching posts from: $url")
      val posts = FileIO.downloadFeed(url)
      (url, posts)
    }

    val output = allPosts
      .map { case (url, posts) => Formatters.formatSubscription(url, posts) }
      .mkString("\n")
      
    println(output)
  }
}
