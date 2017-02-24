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
        xout.writeInt( (int)width);
        xout.write("\"");
        xout.write(" height=\"");
        xout.writeInt( (int)height);
        xout.write("\"");
        xout.write(" viewBox=\"");
        xout.write("0 0 ");
        xout.writeInt( (int)width);
        xout.write(" ");
        xout.writeInt( (int)height);
        xout.write("\"");
        xout.write(" >");
        xout.write("<g>");
  
        pages[i].paintAll(context, new XSVGGraphics(xout) );

        xout.write("</g>");
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
