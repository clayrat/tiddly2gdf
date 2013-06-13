import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import org.htmlcleaner.HtmlCleaner
import java.net.URL

object main {

  val tagColor = "'0,0,0'"
  val linkColor = "'0,100,100'"

  def monoidify(s: String): String = if (s == null) "" else s

  def stripLink(l: String) =
    (if (l.contains("|"))
      l.dropWhile(_ != '|').stripPrefix("|")
    else
      l.stripPrefix("[["))
      .stripSuffix("]]")

  def getGraphFromUrl(url: String): (List[String], List[String]) = {
    val nodes = new ListBuffer[String]()
    val edges = new ListBuffer[String]()
    val linkPattern = new Regex("\\[\\[[^\\]^:]+\\]\\]")
    (new HtmlCleaner).clean(new URL(url)).getElementsByName("div", true).foreach { elem =>
      val title = elem.getAttributeByName("title")
      nodes += title
      monoidify(elem.getAttributeByName("tags")).split(" ").foreach { tag =>
        edges += List(title, tag, "true", tagColor).mkString(",")
      }
      linkPattern.findAllIn(elem.getText).foreach { link =>
        edges += List(title, stripLink(link), "true", linkColor).mkString(",")
      }
    }
    return (nodes.toList, edges.toList)
  }

  def main(args: Array[String]) {
    val graph = getGraphFromUrl("file:///D:/temp/0t2g/test.html")
    val p = new java.io.PrintWriter("D://temp//0t2g//test.gdf")
    p.println("nodedef>name VARCHAR")
    graph._1.foreach(p.println)
    p.println("edgedef>node1 VARCHAR,node2 VARCHAR,directed BOOLEAN,color VARCHAR")
    graph._2.foreach(p.println)
    p.close
  }

}