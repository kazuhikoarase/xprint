package com.d_project.pdf;


/**
 * PDFImage
 * @author Kazuhiko Arase
 */
public class PDFImage extends PDFXObject {

    public PDFImage(byte[] data) {
        super(data);
        put("Subtype", new PDFName("Image") );
    }

    public int getWidth() {
        return ((PDFInteger)get("Width") ).getValue();
    }
    
    public int getHeight() {
        return ((PDFInteger)get("Height") ).getValue();
    }
}


