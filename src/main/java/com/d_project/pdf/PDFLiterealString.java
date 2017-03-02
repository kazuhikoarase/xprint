package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFLiterealString
 * @author Kazuhiko Arase
 */
public class PDFLiterealString extends PDFString {

	public PDFLiterealString(byte[] value) {
        super(value);
    }

	public PDFLiterealString(String value) {
        this(getBytes(value) );
    }
    
    private static byte[] getBytes(String s) {
        try {
            return s.getBytes("ISO-8859-1");
        } catch(IOException e) {
            e.printStackTrace();
            throw new Error();
        }
    }

	public void writePDF(PDFOutputStream out) throws IOException {
        
        byte[] value = getValue();

        out.write('(');

		for (int i = 0; i < value.length; i++) {

            int c = 0xff & value[i];

            switch(c) {

            case '\n' :
			    out.write('\\');
			    out.write('n');
                break;

            case '\r' :
			    out.write('\\');
			    out.write('r');
                break;

            case '\t' :
			    out.write('\\');
			    out.write('t');
                break;

            case '\b' :
			    out.write('\\');
			    out.write('b');
                break;

            case '\f' :
			    out.write('\\');
			    out.write('f');
                break;

            case '(' :
			    out.write('\\');
			    out.write('(');
                break;

            case ')' :
			    out.write('\\');
			    out.write(')');
                break;

            case '\\' :
			    out.write('\\');
			    out.write('\\');
                break;

            default :
                if (0x20 <= c && c <= 0x7e) {
                    // Printable.
			        out.write(value[i]);
                } else {
                    // Not Printable.
			        out.write('\\');
                    out.write('0' + ( (value[i] >>> 6) & 0x07) );
                    out.write('0' + ( (value[i] >>> 3) & 0x07) );
                    out.write('0' + ( (value[i] >>> 0) & 0x07) );
                }
                break;
            }
		}
        out.write(')');
    }
}