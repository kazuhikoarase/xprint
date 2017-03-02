package com.d_project.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * PDFArray
 * @author Kazuhiko Arase
 */
public class PDFArray extends PDFObject {

    private List list;

    public PDFArray() {
        this(new PDFObject[0]);
    }

    public PDFArray(PDFObject[] array) {
        list = new ArrayList();
        for (int i = 0; i < array.length; i++) {
            add(array[i]);
        }
    }

    public void add(PDFObject object) {
        list.add(object);
    }
    
    public PDFObject get(int index) {
        return (PDFObject)list.get(index);
    }

    public int getCount() {
        return list.size();
    }

    protected void writePDF(PDFOutputStream out) throws IOException {

        out.write('[');
        out.write(' ');

        for (int i = 0; i < getCount(); i++) {
            if (i > 0) {
                out.write(' ');
            }
            out.writeObject(get(i) );
        }

        out.write(' ');
        out.write(']');
    }
}
