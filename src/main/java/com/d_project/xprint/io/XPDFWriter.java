package com.d_project.xprint.io;

import java.awt.print.PrinterException;
import java.io.IOException;

import com.d_project.pdf.PDFDocument;
import com.d_project.pdf.writer.PDFWriter;
import com.d_project.xprint.core.AttributeKeys;

/**
 * XPDFWriter
 * @author Kazuhiko Arase
 */
public class XPDFWriter extends AbstractXWriter {

    public XPDFWriter() {
    }
			
    public PDFDocument create(IXWriterListener listener) throws PrinterException, IOException {
    	
    	canceled = false;
    	    
        double width  = pages[0].getNumberAttribute(AttributeKeys.WIDTH);
        double height = pages[0].getNumberAttribute(AttributeKeys.HEIGHT);

		PDFWriter out = new PDFWriter();
		out.setPageSize( (int)width, (int)height);

		for (int i = 0; i < pages.length; i++) {

			out.beginPage();
			pages[i].paintAll(context, new XPDFGraphics(out) );
			out.endPage();
			
			if (canceled) {
				return null;
			}
						
			if (listener != null) {
				listener.onPage(i);
			}
		}

		return out.getDocument();
    }
}
