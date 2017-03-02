package com.d_project.pdf;


/**
 * PDFXObject
 * @author Kazuhiko Arase
 */
public class PDFXObject extends PDFStream {

    public PDFXObject(byte[] data) {
        super(data);
        put("Type", new PDFName("XObject") );
    }
}


