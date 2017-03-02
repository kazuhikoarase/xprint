package com.d_project.pdf;

import java.io.IOException;
import java.io.OutputStream;


/**
 * PDFOutputStream
 * @author Kazuhiko Arase
 */
public class PDFOutputStream {

    private OutputStream out;

    private long position;

    public PDFOutputStream(OutputStream out) {
        this.out = out;
        this.position = 0;
    }

    public long getPosition() {
        return position;
    }

    public void write(int b) throws IOException {
        out.write(b);
        position++;
    }

    public void write(byte[] bytes) throws IOException {
        out.write(bytes);
        position += bytes.length;
    }

    public void print(String s) throws IOException {
        write(s.getBytes("ISO-8859-1") );
    }

    public void println() throws IOException {
        write(0x0a);
    }

    public void println(String s) throws IOException {
        print(s);
        println();
    }

    public void writeObject(PDFObject object) throws IOException {

        if (object.isIndirect() ) {

        	print(String.valueOf(object.getObjectNumber() ) );
            write(' ');
            print(String.valueOf(object.getGenerationNumber() ) );
            write(' ');
            write('R');

        } else {
            object.writePDF(this);
        }
    }

    public void close() throws IOException {
        out.close();
    }
}
