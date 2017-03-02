package com.d_project.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * PDFDocument
 * @author Kazuhiko Arase
 */
public class PDFDocument {

    private List objects;

    private PDFTrailer trailer;

    public PDFDocument() {
        this.objects = new ArrayList();
        this.trailer = new PDFTrailer();
    }

    public void addObject(PDFObject obj) {
        objects.add(obj);
    }

    public int getObjectCount() {
        return objects.size();
    }

    private PDFObject getObject(int index) {
        return (PDFObject)objects.get(index);
    }

    public PDFTrailer getTrailer() {
        return trailer;
    }

    public void save(OutputStream ostream) throws IOException {

        //----------------
        // prepare

        // renumber all objects.
        for (int i = 0; i < getObjectCount(); i++) {
            getObject(i).setObjectNumber(i + 1);
        }

        trailer.setSize(new PDFInteger(getObjectCount() + 1) );

        //----------------
        // outpur stream

        PDFOutputStream pdfOut = new PDFOutputStream(ostream);

        //----------------
        // header

        pdfOut.println("%PDF-1.3");

        //----------------
        // body

        long[] pos = new long[getObjectCount()];

        for (int i = 0; i < getObjectCount(); i++) {

            pos[i] = pdfOut.getPosition();

            pdfOut.print(String.valueOf(getObject(i).getObjectNumber() ) );
            pdfOut.write(' ');
            pdfOut.print(String.valueOf(getObject(i).getGenerationNumber() ) );
            pdfOut.write(' ');
            pdfOut.println("obj");

            getObject(i).writePDF(pdfOut);

            pdfOut.println();
            pdfOut.println("endobj");
        }

        //----------------
        // xref

        long startxref = pdfOut.getPosition();

        pdfOut.println("xref");

        pdfOut.write('0');
        pdfOut.write(' ');
        pdfOut.println(String.valueOf(getObjectCount() + 1) );
        
        pdfOut.print(formatXref(0, 0xffff, 'f') );
        for (int i = 0; i < getObjectCount(); i++) {
            pdfOut.print(formatXref(pos[i], 0, 'n') );
        }

        //----------------
        // Trailer

        pdfOut.println("trailer");
        pdfOut.writeObject(trailer);
        pdfOut.println();
        pdfOut.println("startxref");
        pdfOut.println(String.valueOf(startxref) );

        //----------------
        // EOF

        pdfOut.println("%%EOF");
    }


	
    private static String formatXref(long pos, int n, char c) {

        StringBuffer buffer = new StringBuffer();

        buffer.append(Util.format(pos, 10) );
        buffer.append(' ');
        buffer.append(Util.format(n, 5) );
        buffer.append(' ');
        buffer.append(c);

        // eol (fixed, CRLF)
        buffer.append( (char)0x0d);
        buffer.append( (char)0x0a);

        return buffer.toString();
    }
}
