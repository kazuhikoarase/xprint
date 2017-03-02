package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFNull
 * @author Kazuhiko Arase
 */
public class PDFNull extends PDFObject {

    public PDFNull() {
    }

    protected void writePDF(PDFOutputStream out) throws IOException {
        out.print("null");
    }
}
