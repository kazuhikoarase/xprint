package com.d_project.xprint.io;

import java.awt.print.PrinterException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

import com.d_project.xprint.core.AttributeKeys;

/**
 * XRawWriter
 * @author Kazuhiko Arase
 */
public class XRawWriter extends AbstractXWriter {

    public XRawWriter() {
    }

    public void create(OutputStream out, IXWriterListener listener) throws PrinterException, IOException {

        DeflaterOutputStream defOut = new DeflaterOutputStream(out);
        
        try {

	    	DataOutputStream objOut = new DataOutputStream(defOut);
        
	        try {

	        	canceled = false;

	            double width  = pages[0].getNumberAttribute(AttributeKeys.WIDTH);
	            double height = pages[0].getNumberAttribute(AttributeKeys.HEIGHT);

	        	objOut.writeDouble(width);
	        	objOut.writeDouble(height);
	        	objOut.writeInt(pages.length);
		        
				for (int i = 0; i < pages.length; i++) {
		
					pages[i].paintAll(context, new XRawGraphics(objOut) );
	
					objOut.writeByte(XRawCommand.CMD_TYPE_END);
					
					if (canceled) {
						return;
					}
								
					if (listener != null) {
						listener.onPage(i);
					}
				}

	        } finally {
	        	objOut.flush();
	        }

        } finally {
        	defOut.finish();
        	defOut.flush();
        }
    }
}
