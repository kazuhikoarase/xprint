package com.d_project.pdf;


/**
 * PDFString
 * @author Kazuhiko Arase
 */
public abstract class PDFString extends PDFObject {

	private byte[] value;

    public PDFString(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }
}
