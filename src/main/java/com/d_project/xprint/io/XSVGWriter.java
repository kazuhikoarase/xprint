package com.d_project.xprint.io;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.OutputStream;

import com.d_project.xprint.core.AttributeKeys;

/**
 * XSVGWriter
 * @author Kazuhiko Arase
 */
public class XSVGWriter extends AbstractXWriter {

  public XSVGWriter() {
  }

  public void create(
      OutputStream out,
      IXWriterListener listener
  ) throws PrinterException, IOException {

    XSVGOutputStream xout = new XSVGOutputStream(out);

    try {

      canceled = false;

      double width  = pages[0].getNumberAttribute(AttributeKeys.WIDTH);
      double height = pages[0].getNumberAttribute(AttributeKeys.HEIGHT);

      for (int i = 0; i < pages.length; i++) {

        xout.write("<svg version=\"1.1\"");
        xout.write(" xmlns=\"http://www.w3.org/2000/svg\"");
        xout.write(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"");
        xout.write(" width=\"");
        xout.writeDouble(width);
        xout.write("\"");
        xout.write(" height=\"");
        xout.writeDouble(height);
        xout.write("\" >");
  
        pages[i].paintAll(context, new XSVGGraphics(xout) );

        xout.write("</svg>");
        
        if (canceled) {
          return;
        }
              
        if (listener != null) {
          listener.onPage(i);
        }
      }

    } finally {
      xout.flush();
    }
  }
}
