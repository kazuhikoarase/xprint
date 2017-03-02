package com.d_project.pdf;


/**
 * PDFCatalog
 * @author Kazuhiko Arase
 */
public class PDFCatalog extends PDFDictionary {

    public PDFCatalog() {
        put("Type", new PDFName("Catalog") );
    }

    public void setPages(PDFPageTreeNode pages) {
        put("Pages", pages);
    }

    public PDFPageTreeNode getPages() {
        return (PDFPageTreeNode)get("Pages");
    }

    public void setOutlines(PDFOutlines outlines) {
        put("Outlines", outlines);
    }

    public PDFOutlines getOutlines() {
        return (PDFOutlines)get("Outlines");
    }
}