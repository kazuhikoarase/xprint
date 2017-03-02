package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFHexadecimalString
 * @author Kazuhiko Arase
 */
public class PDFHexadecimalString extends PDFString {

	public PDFHexadecimalString(byte[] value) {
        super(value);
    }

	public void writePDF(PDFOutputStream out) throws IOException {

        byte[] value = getValue();

        out.write('<');
		for (int i = 0; i < value.length; i++) {
			out.write(Util.toHexadecimalChar( (value[i] >>> 4) & 0x0f) );
			out.write(Util.toHexadecimalChar(value[i] & 0x0f) );
		}
        out.write('>');
    }


}

