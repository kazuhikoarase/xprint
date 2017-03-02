package com.d_project.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;


/**
 * PDFDeflateStream
 * @author Kazuhiko Arase
 */
public class PDFDeflateStream extends PDFStream {

    public PDFDeflateStream(byte[] data) throws IOException {
        super(deflate(data) );
        put("Filter", new PDFName("FlateDecode") );
    }

    private static byte[] deflate(byte[] data) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DeflaterOutputStream dout = new DeflaterOutputStream(bout);
        dout.write(data);
        dout.close();
        return bout.toByteArray();
    }
}