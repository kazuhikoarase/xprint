package com.d_project.pdf;


/**
 * PDFRectangle
 * @author Kazuhiko Arase
 */
public class PDFRectangle extends PDFArray {
        
    public PDFRectangle(int lowerLeftX, int lowerLeftY, int upperRightX, int upperRightY) {
        add(new PDFInteger(lowerLeftX) );
        add(new PDFInteger(lowerLeftY) );
        add(new PDFInteger(upperRightX) );
        add(new PDFInteger(upperRightY) );
    }
}