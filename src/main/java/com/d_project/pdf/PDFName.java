package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFName
 * @author Kazuhiko Arase
 */
public class PDFName extends PDFObject {

    private String name;

    public PDFName(String name) {
        this.name = name;
    }

    public int hashCode() {
        return name.hashCode();
    }
    
    public boolean equals(Object o) {
        if (o != null && o instanceof PDFName) {
            return ( (PDFName)o).name.equals(name);
        }
        return false;
    }

    protected void writePDF(PDFOutputStream out) throws IOException {

        out.write('/');

        byte[] value = name.getBytes("ISO-8859-1");

        for (int i = 0; i < value.length; i++) {

            if (33 <= value[i] && value[i] <= 126) {
                out.write(value[i]);
            } else {
                out.write('#');
			    out.write(Util.toHexadecimalChar( (value[i] >>> 4) & 0x0f) );
			    out.write(Util.toHexadecimalChar(value[i] & 0x0f) );
            }
        }
    }
}



