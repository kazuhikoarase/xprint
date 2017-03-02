package com.d_project.pdf;


/**
 * PDFTrailer
 * @author Kazuhiko Arase
 */
public class PDFTrailer extends PDFDictionary {

    public PDFTrailer() {
    }

    public void setRoot(PDFCatalog root) {
        put("Root", root);
    }

    public PDFCatalog getRoot() {
        return (PDFCatalog)get("Root");
    }

    public void setSize(PDFInteger size) {
        put("Size", size);
    }

    public PDFInteger getSize() {
        return (PDFInteger)get("Size");
    }
}



