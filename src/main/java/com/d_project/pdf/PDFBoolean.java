package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFBoolean
 * @author Kazuhiko Arase
 */
public class PDFBoolean extends PDFObject {
    
    private boolean b;

    public PDFBoolean(boolean b) {
        this.b = b;
    }

    protected void writePDF(PDFOutputStream out) throws IOException {
        out.print(b ? "true" : "false");
    }
}
