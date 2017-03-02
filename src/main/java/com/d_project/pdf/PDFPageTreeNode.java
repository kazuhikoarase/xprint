package com.d_project.pdf;


/**
 * PDFPageTreeNode
 * @author Kazuhiko Arase
 */
public class PDFPageTreeNode extends PDFDictionary {

    private PDFArray kids;

    public PDFPageTreeNode() {
        put("Type", new PDFName("Pages") );
        put("Kids", this.kids = new PDFArray() );
        put("Count", new PDFInteger(0) );
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

    public void addPage(PDFPage page) {
        page.put("Parent", this);
        kids.add(page);
        put("Count", new PDFInteger(kids.getCount() ) );
    }

    public PDFPage getPage(int index) {
        return (PDFPage)kids.get(index);
    }

    public int getPageCount() {
        return kids.getCount();
    }
}