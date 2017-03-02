package com.d_project.pdf;


/**
 * PDFOutlines
 * @author Kazuhiko Arase
 */
public class PDFOutlines extends PDFOutlineDictionary {

    public PDFOutlines() {
        put("Type", new PDFName("Outlines") );
    }
}
