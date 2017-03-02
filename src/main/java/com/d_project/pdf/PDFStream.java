package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFStream
 * @author Kazuhiko Arase
 */
public class PDFStream extends PDFDictionary {

    private byte[] data;

    public PDFStream(byte[] data) {
        this.data = data;
        put("Length", new PDFInteger(data.length) );
    }

    public byte[] getData() {
        return data;
    }

    public void writePDF(PDFOutputStream out) throws IOException {

        super.writePDF(out);

        if (data != null) {
            out.println();
            out.println("stream");
            out.write(data);
            out.println();
            out.println("endstream");
        }
    }
}
