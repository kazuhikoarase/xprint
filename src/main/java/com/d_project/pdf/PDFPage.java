package com.d_project.pdf;


/**
 * PDFPage
 * @author Kazuhiko Arase
 */
public class PDFPage extends PDFDictionary {

    public PDFPage() {
        put("Type", new PDFName("Page") );
    }

    public void setContents(PDFStream contents) {
        put("Contents", contents);
    }

    public PDFStream getContents() {
        return (PDFStream)get("Contents");
    }

    public void setResources(PDFDictionary resources) {
        put("Resources", resources);
    }

    public PDFDictionary getResources() {
        return (PDFDictionary)get("Resources");
    }

    public void setMediaBox(PDFRectangle mediaBox) {
        put("MediaBox", mediaBox);
    }

    public PDFRectangle getMediaBox() {
        return (PDFRectangle)get("MediaBox");
    }
}
