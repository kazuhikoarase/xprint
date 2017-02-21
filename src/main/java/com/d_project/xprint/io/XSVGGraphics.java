package com.d_project.xprint.io;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.d_project.xprint.core.AbstractXGraphics;

/**
 * XSVGGraphics
 * @author Kazuhiko Arase
 */
public class XSVGGraphics extends AbstractXGraphics {

  private XSVGOutputStream out;
  private double tx;
  private double ty;
  private Color color;

  public XSVGGraphics(XSVGOutputStream out) {
    this.out = out;
    this.tx = 0;
    this.ty = 0;
    this.color = Color.BLACK;
  }

  @Override
  public void translate(double x, double y) {
    tx += x;
    ty += y;
  }

  @Override
  public void fill(Shape shape) {

    try {

      PathIterator it = shape.getPathIterator(null);
      double[] coords = new double[6];

      out.write("<path");

      switch(it.getWindingRule() ) {
      case PathIterator.WIND_NON_ZERO :
        out.write(" fill-rule=\"nonzero\"");
        break;
      case PathIterator.WIND_EVEN_ODD :
        out.write(" fill-rule=\"evenodd\"");
        break;
      default :
        break;
      }

      out.write(" stroke=\"none\"");

      out.write(" fill=\"RGB(");
      out.write(String.valueOf(color.getRed() ) );
      out.write(',');
      out.write(String.valueOf(color.getGreen() ) );
      out.write(',');
      out.write(String.valueOf(color.getBlue() ) );
      out.write(")\" d=\"");

      while (!it.isDone() ) {

        switch(it.currentSegment(coords) ) {

        case PathIterator.SEG_MOVETO :
          out.write('M');
          writePoint(coords[0], coords[1]);
          break;

        case PathIterator.SEG_LINETO :
          out.write('L');
          writePoint(coords[0], coords[1]);
          break;

        case PathIterator.SEG_QUADTO :
          out.write('Q');
          writePoint(coords[0], coords[1]);
          writePoint(coords[2], coords[3]);
          break;

        case PathIterator.SEG_CUBICTO :
          out.write('C');
          writePoint(coords[0], coords[1]);
          writePoint(coords[2], coords[3]);
          writePoint(coords[4], coords[5]);
          break;

        case PathIterator.SEG_CLOSE :
          out.write('Z');
          break;

        default :
          break;
        }

        it.next();
      }
      out.write("\" />");

    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writePoint(double x, double y) throws IOException {
    out.write(' ');
    out.writeDouble(x + tx);
    out.write(' ');
    out.writeDouble(y + ty);
  }

  @Override
  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public void drawImage(BufferedImage image,
      double dx1, double dy1, double dx2, double dy2) {

    try {

      out.write("<image x=\"");
      out.writeDouble(dx1 + tx);
      out.write("\" y=\"");
      out.writeDouble(dy1 + ty);
      out.write("\" width=\"");
      out.writeDouble(dx2 - dx1);
      out.write("\" height=\"");
      out.writeDouble(dy2 - dy1);
      out.write("\" xlink:href=\"data:image/jpg;base64,");

      @SuppressWarnings("resource")
      Base64EncodeOutputStream bout = new Base64EncodeOutputStream(out);
      try {
        for (byte b : ImageUtil.getJpegData(image, 1.0, 1.0f) ) {
          bout.write(b);
        }
      } finally {
        bout.flush();
      }

      out.write("\" />");

    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
