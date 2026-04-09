import fileio.FileIO.Post // importo para tener el tipo Post

object Formatters {

  def filterPosts(posts: List[Post]): List[Post] = {
    posts.filter {case (_, title, selftext, _, _, _) =>
      // Filtramos los posts que no tienen title ni selftext vacío
      (title.trim.nonEmpty) && (selftext.trim.nonEmpty) 
    }
  }

  def formatSubscription(name: String,score: Int, repeatedWord: (String, Int), posts: List[Post]): String = {
    val header = s"""
      |${"-" * 80}
      |${" " * 31}Suscripcion $name
      |${"-" * 80}
      |Score total: $score
      |Palabra mas repetida: ${repeatedWord._1}, Ocurrencias: ${repeatedWord._2}
      |Primeros 5 posts:
      |""".stripMargin
    val formattedPosts = posts.map {
      case (subreddit, title, selftext, date, score, url) =>
        s"""
        |Fecha: [$date]
        |Titulo: $title
        |URL: $url
        |${"-" * 40}""".stripMargin
    }

    //le añadi un barra n mas para que haya mas espacio entre cada post
    header + "\n" + formattedPosts.mkString("\n\n")
  }

  def extractWords(content: String): List[String] = {
    // Armamos arreglo sacando cosas que no sean palabras
    val arregloPalabras = content.split("\\W+")
    // Convertimos a lista
    val listaPalabras = arregloPalabras.toList
    // Sacamos las palabras vacias
    val listaPalabrasFiltrado = listaPalabras.filter(_.nonEmpty)
    
    listaPalabrasFiltrado
  }

  def countWords(posts: List[Post]): Map[String, Int] = {
    // Filtro que no tenga el texto vacío
    val listaPostFiltrado = filterPosts(posts)

    listaPostFiltrado match {
      case Nil => Map.empty
      case post :: _ =>
        // Armo la lista con las words
        val listaPalabras = extractWords(post._3) // Selftext 

        val stopwords = List( "the", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "re", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves")

        // Ahora por cada elemento tengo que validar si empieza con mayúscula
        val mayusFiltrada = listaPalabras.filter(w => w.head.isUpper)
        // Valido si pertenece a la lista de stopwords
        val stopwordsFiltradas = mayusFiltrada.filter(w => !stopwords.contains(w.toLowerCase))
        // Agrupo por repeticiones de la palabra
        val mapped = stopwordsFiltradas.groupBy(identity)

        val mappedLen = mapped.map { case (palabra, lista)=>
          (palabra, lista.length)
        }
        mappedLen
    }
  }

  def scoring(posts: List[Post]): Int = {
      val sum = posts.foldLeft(0) { 
        case (acumulator, (_, _, _, _, score, _)) =>
        acumulator + score
      }
      sum
  }
}
