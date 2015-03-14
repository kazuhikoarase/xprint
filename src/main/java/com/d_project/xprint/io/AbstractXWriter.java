package com.d_project.xprint.io;

import com.d_project.xprint.core.XNode;
import com.d_project.xprint.core.XNodeContext;

/**
 * AbstractXWriter
 * @author Kazuhiko Arase
 */
public abstract class AbstractXWriter {
	
    protected XNodeContext context = null;
    protected XNode[] pages = null;
    protected boolean canceled = false;
    
    protected AbstractXWriter() {
    }

    public void setPages(XNodeContext context, XNode[] pages) {
    	this.context = context;
        this.pages = pages;
    }
    
    public XNode[] getPages() {
        return pages;
    }
	
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
	public boolean isCanceled() {
		return canceled;
	}

}
