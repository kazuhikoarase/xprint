package com.d_project.xprint.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class XSVGOutputStream extends FilterOutputStream {
  private DecimalFormat formatter = new DecimalFormat("0.0000");
  public XSVGOutputStream(OutputStream out) {
    super(out);
  }
  public void write(String s) throws IOException {
    out.write(s.getBytes("ISO-8859-1") );
  }
  public void writeDouble(double n) throws IOException {
    write(formatter.format(n) );
  }
}
