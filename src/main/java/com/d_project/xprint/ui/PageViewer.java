package com.d_project.xprint.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.d_project.xprint.core.AttributeKeys;
import com.d_project.xprint.core.IXGraphics;
import com.d_project.xprint.core.XAWTGraphics;
import com.d_project.xprint.core.XNode;
import com.d_project.xprint.core.XNodeContext;

/**
 * PageViewer
 * @author Kazuhiko Arase
 */
public class PageViewer extends JComponent {

	private XNodeContext context;
    private XNode[] pages;
    private int pageIndex;
    private int padding = 8;
    private double scale = 1;
    private Point dragPoint;
    private boolean showBoundingBox;

    public PageViewer() {

    	this.context = null;

        setAutoscrolls(true);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Rectangle rect = getVisibleRect();
                rect.x -= (e.getX() - dragPoint.x);
                rect.y -= (e.getY() - dragPoint.y);
                scrollRectToVisible(rect);
            }
        });

    }
    
    public void setScale(double scale) {
        this.scale = scale;
        repaint();
    }
    
    public double getScale() {
        return scale;
    }
    
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        repaint();
    }
    
    public int getPageIndex() {
        return pageIndex;
    }
    
    public int getPageCount() {
        return (pages != null)? pages.length : 0;
    }

    public void setPages(XNodeContext context, XNode[] pages) {
    	this.context = context;
        this.pages = pages;
       	this.pageIndex = 0;

        repaint();
    }
    
    public int getPadding() {
        return padding;
    }    
    
    public Dimension getPreferredSize() {
        
        double width  = padding * 2;
        double height = padding * 2;
        
        XNode page = getCurrentPage();

        if (page != null) {
            width  += page.getNumberAttribute(AttributeKeys.WIDTH) * scale;
            height += page.getNumberAttribute(AttributeKeys.HEIGHT) * scale; 
        }
        
        return new Dimension( (int)width, (int)height);
    }

    public XNode getCurrentPage() {
        if (pages != null && 0 <= pageIndex && pageIndex < pages.length) {
            return pages[pageIndex];
        } else {
            return null;
        }
    }    

    public void setShowBoundingBox(boolean showBoundingBox) {
    	this.showBoundingBox = showBoundingBox;
    	repaint();
    }
    
	public boolean isShowBoundingBox() {
		return showBoundingBox;
	}

    public void paint(Graphics g) {

    	Graphics2D g2d = (Graphics2D)g;

    	paintBackground(g2d);
    	
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.translate(padding, padding);
        g2d.scale(scale, scale);

        try {

        	paintPage(new XAWTGraphics(g2d) );

        } catch(Throwable t) {

        	setPages(null, null);
        	
        	MainFrame.getInstance().handleThrowable(t);
        }
    }
    
    private void paintBackground(Graphics2D g) {
    	Dimension size = getSize();
    	g.setColor(new Color(0xcccccc) );
    	g.fill(new Rectangle2D.Double(0, 0, size.width, size.height) );
//    	g.drawLine(0, 0, size.width, size.height);
//    	g.drawLine(size.width, 0, 0, size.height);
    }
    
    private void paintPage(IXGraphics g) {
        
        XNode page = getCurrentPage();

        if (page != null) {

	        g.setShowBoundingBox(showBoundingBox);
    
            double width  = page.getNumberAttribute(AttributeKeys.WIDTH);
            double height = page.getNumberAttribute(AttributeKeys.HEIGHT);
    
            g.setColor(Color.white);
            g.fill(new Rectangle2D.Double(0, 0, width, height) );
    
            page.paintAll(context, g);
        }
    }
}
