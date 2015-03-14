package com.d_project.xprint.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * XPrintable
 * @author Kazuhiko Arase
 */
public class XPrintable implements Printable {
    
	private XNodeContext context;
    
    private XNode[] pages;
    
    public XPrintable() {
    	this.context = null;
    	this.pages = null;
    }

    public void setPages(XNodeContext context, XNode[] pages) {
    	this.context = context;
        this.pages = pages;
    }
    
    public XNode[] getPages() {
        return pages;
    }
    
    public PageFormat getDefaultPageFormat() {
    	
        double width  = pages[0].getNumberAttribute(AttributeKeys.WIDTH);
        double height = pages[0].getNumberAttribute(AttributeKeys.HEIGHT);

        Paper paper = new Paper();
        // 用紙サイズ
        paper.setSize(
    		Math.min(width, height), // 短辺を幅とする。
    		Math.max(width, height)  // 長辺を高さとする。
    	);
        // 用紙の印字可能領域設定 = 最大
        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight() );
        
        PageFormat format = new PageFormat();
        format.setOrientation( (width < height)?
        		PageFormat.PORTRAIT :
        		PageFormat.LANDSCAPE);
        format.setPaper(paper);
        
        return format;
    }

    public int print(Graphics g, PageFormat format, int pageIndex)
    throws PrinterException {

    	Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        return print(new XAWTGraphics(g2d), format, pageIndex);
    }
    
    private int print(IXGraphics g, PageFormat format, int pageIndex)
    throws PrinterException {

        
        if (pageIndex >= pages.length) {
            // 1ページ目 (pageIndex == 0) 以外は存在しない。
            return Printable.NO_SUCH_PAGE;
        }

        pages[pageIndex].paintAll(context, g);
           
        return Printable.PAGE_EXISTS;
    }

}
