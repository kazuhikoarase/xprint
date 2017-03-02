package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFInteger
 * @author Kazuhiko Arase
 */
public class PDFInteger extends PDFObject {

    private int value;

    public PDFInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void writePDF(PDFOutputStream out) throws IOException {
        out.print(String.valueOf(value) );
    }
}
