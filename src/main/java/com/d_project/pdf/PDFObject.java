package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFObject
 * @author Kazuhiko Arase
 */
public abstract class PDFObject {

    private int objectNumber;
    private int generationNumber;

    public PDFObject() {
        this.objectNumber = 0;
        this.generationNumber = 0;
    }
    
    public void setObjectNumber(int objectNumber) {
        this.objectNumber = objectNumber;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public int getObjectNumber() {
        return objectNumber;
    }

    public boolean isIndirect() {
        return (objectNumber != 0);
    }

    protected abstract void writePDF(PDFOutputStream out) throws IOException;
}
