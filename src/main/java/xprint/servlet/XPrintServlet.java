package xprint.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xprint.ResourceManager;
import xprint.XPrint;

/**
 * XPrintServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class XPrintServlet extends HttpServlet {

  private static final String PDF_SUFFIX = ".pdf";

  private String configRoot;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ResourceManager.setResourceManager(new ResourceManager() {
      @Override
      public InputStream getResourceAsStream(String name) {
        try {
          if (!name.startsWith("/") ) {
            name = "/" + name;
          }
          String path = getServletContext().getRealPath(configRoot + name);
          if (!new File(path).exists() ) {
            path = getServletContext().getRealPath(name);
          }
          return new BufferedInputStream(new FileInputStream(path) );
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    configRoot = config.getInitParameter("config-root");
  }

  protected String getFormName(Map<String,String> params) {
    String formName = params.get("form-name");
    if (!formName.matches("^[A-Za-z0-9\\-]+$") ) {
      throw new IllegalArgumentException(formName);
    }
    return formName;
  }

  protected File toFile(String path) {
    return new File(getServletContext().getRealPath(path) );
  }

  @Override
  protected void doPost(
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ServletException, IOException {

    Map<String,String> params = new HashMap<String, String>();
    String data = request.getParameter("data");
    for (String kv : data.split("&") ) {
      int index = kv.indexOf("=");
      if (index != -1) {
        params.put(kv.substring(0, index),
            URLDecoder.decode(kv.substring(index + 1), "UTF-8") );
      }
    }

    final String formName = getFormName(params);

    final File formFile = toFile(configRoot + "/" + formName + "-form.xml");

    if (!formFile.exists() ) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND,
          request.getRequestURI() );
      return;
    }

    final XPrint xp = new XPrint();

    try {
      xp.load(formFile, Util.mapToXml(params) );
    } catch(Exception e) {
      throw new ServletException(e);
    }

    if ("SVG".equals(params.get("output") ) ) {
      outputSVG(request, response, params, xp);
    } else {
      outputPDF(request, response, params, xp);
    }
  }

  protected void outputSVG(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final Map<String,String> params,
    final XPrint xp
  ) throws ServletException, IOException {

    response.reset();
    response.setContentType("application/xml");

    Util.outputGZIP(request, response, new Util.OutputHandler() {
      @Override
      public void handle(OutputStream out) throws Exception {
        out.write("<svg-pages>".getBytes("ISO-8859-1") );
        xp.outputSVG(out);
        out.write("</svg-pages>".getBytes("ISO-8859-1") );
      }
    });
  }

  protected void outputPDF(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final Map<String,String> params,
    final XPrint xp
  ) throws ServletException, IOException {

    response.reset();
    response.setContentType("application/octet-stream");

    String filename = params.get("filename");
    filename = (filename != null)? filename.trim() : "";

    // 未指定の場合、既定のファイル名
    if (filename.length() == 0) {
      filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date() );
    }

    // 拡張子付与
    if (!filename.endsWith(PDF_SUFFIX) ) {
      filename = filename + PDF_SUFFIX;
    }

    response.setHeader("Content-Disposition",
        "attachment;" + Util.encodeFilename(request, filename) );

    OutputStream out = new BufferedOutputStream(response.getOutputStream() );
    try {
      xp.outputPDF(out);
    } catch(Exception e) {
      throw new ServletException(e);
    } finally {
      out.close();
    }
  }
}
