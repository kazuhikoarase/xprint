package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFTextString
 * @author Kazuhiko Arase
 */
public class PDFTextString extends PDFLiterealString {

    public PDFTextString(String s) {
        super(getBytes(s) );
    }

    private static byte[] getBytes(String s) {
        try {
            return s.getBytes("UnicodeBig");
        } catch(IOException e) {
            e.printStackTrace();
            throw new Error();
        }
    }
}
