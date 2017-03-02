package com.d_project.pdf;

import java.util.ArrayList;
import java.util.List;


/**
 * PDFOutlineDictionary
 * @author Kazuhiko Arase
 */
public abstract class PDFOutlineDictionary extends PDFDictionary {

    private List items;

    protected PDFOutlineDictionary() {
        this.items = new ArrayList();
    }

    public void addItem(PDFOutlineItem item) {

        items.add(item);

        // parent
        item.put("Parent", this);

        // first
        if (getItemCount() == 1) {
            put("First", item);
        }

        // last
        put("Last", item);

        if (getItemCount() > 1) {
            PDFOutlineItem prev = getItem(getItemCount() - 2);
            item.put("Prev", prev);
            prev.put("Next", item);
        }
    }

    public PDFOutlineItem getItem(int index) {
        return (PDFOutlineItem)items.get(index);
    }

    public int getItemCount() {
        return items.size();
    }
}