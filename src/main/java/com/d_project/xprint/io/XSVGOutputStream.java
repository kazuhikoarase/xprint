package com.d_project.xprint.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * XSVGOutputStream
 * @author Kazuhiko Arase
 */
public class XSVGOutputStream extends FilterOutputStream {
  private static final String HEX_CHARS = "0123456789ABCDEF";
  private final DecimalFormat formatter = new DecimalFormat("0.000");
  public XSVGOutputStream(OutputStream out) {
    super(out);
  }
  public void write(final String s) throws IOException {
    out.write(s.getBytes("ISO-8859-1") );
  }
  public void writeHex(final int b) throws IOException {
    out.write(HEX_CHARS.charAt( (b >> 4) & 0xf) );
    out.write(HEX_CHARS.charAt(b & 0xf) );
  }
  public void writeInt(final int n) throws IOException {
    write(String.valueOf(n) );
  }
  public void writeDouble(final double n) throws IOException {
    write(formatter.format(n) );
  }
}
