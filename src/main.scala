import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import org.htmlcleaner.HtmlCleaner
import java.net.URL

object main {

  def getGraphFromUrl(url: String): (List[String], List[String]) = {
    val nodes = new ListBuffer[String]()
    val edges = new ListBuffer[String]()
    val cleaner = new HtmlCleaner
    val props = cleaner.getProperties
    val rootNode = cleaner.clean(new URL(url))
    val elements = rootNode.getElementsByName("div", true)
    val pattern = new Regex("\\[\\[[^\\]^:]+\\]\\]")
    for (elem <- elements) {
      val title = elem.getAttributeByName("title")
      nodes += title
      val tags = elem.getAttributeByName("tags")
      val tags2 = if (tags == null) "" else tags
      for (tag <- tags2.split(" ")) {
        edges += title + "," + tag + ",true,'0,0,0'"
      }
      val links = (pattern findAllIn elem.getText)
      for (link <- links) {
        val srlink = if (link.contains("|")) link.dropWhile(c => c != '|').stripPrefix("|") else link.stripPrefix("[[")
        edges += title + "," + srlink.stripSuffix("]]") + ",true,'0,100,100'"
      }
    }
    return (nodes.toList, edges.toList)
  }

  def main(args: Array[String]) {
    var graph = getGraphFromUrl("file:///D:/temp/0t2g/test.html")
    val p = new java.io.PrintWriter("D://temp//0t2g//test.gdf")
    p.println("nodedef>name VARCHAR")
    for (node <- graph._1) {
      p.println(node)
    }
    p.println("edgedef>node1 VARCHAR,node2 VARCHAR,directed BOOLEAN,color VARCHAR")
    for (edge <- graph._2) {
      p.println(edge)
    }
    p.close

  }

}