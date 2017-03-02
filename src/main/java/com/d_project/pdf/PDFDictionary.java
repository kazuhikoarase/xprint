package com.d_project.pdf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * PDFDictionary
 * @author Kazuhiko Arase
 */
public class PDFDictionary extends PDFObject {

    private Map map;

    public PDFDictionary() {
        map = new HashMap();
    }

    public void put(String key, PDFObject val) {
        map.put(new PDFName(key), val);
    }

    public PDFObject get(String key) {
        return (PDFObject)map.get(new PDFName(key) );
    }

    protected void writePDF(PDFOutputStream out) throws IOException {

        boolean first = true;

        out.print("<<");
        out.write(' ');

        Iterator it = map.keySet().iterator();

        while (it.hasNext() ) {

            if (!first) {
                out.write(' ');
            }

            PDFObject key = (PDFObject)it.next();
            PDFObject val = (PDFObject)map.get(key);

            out.writeObject(key);
            out.write(' ');
            out.writeObject(val);

            first = false;
        }

        out.write(' ');
        out.print(">>");
    }
}