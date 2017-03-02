package xprint.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Util
 * @author Kazuhiko Arase
 */
public class Util {

  private Util() {
  }

  public static String encodeFilename(
    final HttpServletRequest request, 
    final String filename
  ) throws IOException {
    final String ua = request.getHeader("User-Agent");
    if (ua != null && ua.contains("MSIE") ) {
      // ※IEの仕様により、日本語ファイル名は MS932 に固定
      // http://support.microsoft.com/default.aspx?scid=kb;ja;436616
      // ftp://ftp.rfc-editor.org/in-notes/rfc2231.txt
      return "filename=\"" +
        new String(filename.getBytes("MS932"), "ISO-8859-1") +
        "\"";
    }
    return "filename*=UTF-8''" + 
      URLEncoder.encode(filename, "UTF-8");
  }

  public static Document mapToXml(Map<String,String> map) throws Exception {

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Node root = doc.appendChild(doc.createElement("root") );

    for (Entry<String,String> entry : map.entrySet() ) {
    
      String[] names = entry.getKey().split("\\.");
      Node current = root;

      for (String name : names) {
        Matcher mat = Pattern.compile("([a-zA-Z0-9_\\-]+)(?:\\[(\\d+)\\])?").matcher(name);
        if (!mat.find() ) {
          throw new Exception("invalid name:" + name);
        }
        String nodeName = mat.group(1);
        int index = (mat.group(2) != null)? Integer.parseInt(mat.group(2) ) : 0;
        current = getIndexedNode(current, nodeName, index);
      }

      current.appendChild(doc.createTextNode(entry.getValue() ) );
    }

    return doc;
  }

  private static Node getIndexedNode(Node current, String nodeName, int index) {
    NodeList nodes = current.getChildNodes();
    int count = 0;
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodeName.equals(nodes.item(i).getNodeName() ) ) {
        if (count == index) {
          // found
          return nodes.item(i);
        }
        count++;
      }
    }

    // count = 2, index = 2 => 1
    // count = 2, index = 3 => 2

    int inc = index - count + 1;
    if (inc < 1) {
      throw new IllegalStateException("inc:" + inc);
    }
    Document doc = current.getOwnerDocument();
    Node node = null;
    for (int i = 0; i < inc; i++) {
      node = current.appendChild(doc.createElement(nodeName) );
    }
    return node;
  }
}
