package com.d_project.pdf;


/**
 * PDFOutlineItem
 * @author Kazuhiko Arase
 */
public class PDFOutlineItem extends PDFOutlineDictionary {

    public PDFOutlineItem(String title, PDFPage page) {

        put("Title", new PDFTextString(title) );

        PDFArray dest = new PDFArray();
        dest.add(page);
        dest.add(new PDFName("XYZ") );
        dest.add(new PDFInteger(0) );
        dest.add(new PDFInteger(0) );
        dest.add(new PDFNull() );
        put("Dest", dest);
    }
}
