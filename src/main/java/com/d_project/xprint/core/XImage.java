package com.d_project.xprint.core;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * XImage
 * @author Kazuhiko Arase
 */
public class XImage extends XNode {

    public XImage() {
        super(NodeNames.IMAGE_NODE);
    }

    public Dimension2D getContentSize(XNodeContext context) {

    	String src = getAttribute(AttributeKeys.SRC);
    	
    	IDrawableImage image = context.getImage(src);
        
        double width  = 0;
        double height = 0;
        
        double realW = image.getWidth();
        double realH = image.getHeight();

        double iw = getNumberAttribute(AttributeKeys.WIDTH);
        double ih = getNumberAttribute(AttributeKeys.HEIGHT);

        if (iw == 0 && ih == 0) {
            iw = realW;
            ih = realH;
        } else if (iw == 0 && ih != 0) {
            iw = ih * realW / realH;
        } else if (iw != 0 && ih == 0) {
            ih = iw * realH / realW;
        }
                
        width  += iw;
        height += ih;
                        
        return new Size2D(width, height);
    }

    public void paint(XNodeContext context, IXGraphics g) {

    	String src = getAttribute(AttributeKeys.SRC);

    	IDrawableImage image = context.getImage(src);
        Rectangle2D rect = getBounds();

        double realW = image.getWidth();
        double realH = image.getHeight();

        double hPad = getNumberAttribute(AttributeKeys.PADDING_LEFT)
   	 		+ getNumberAttribute(AttributeKeys.PADDING_RIGHT);
        double vPad = getNumberAttribute(AttributeKeys.PADDING_TOP)
   	 		+ getNumberAttribute(AttributeKeys.PADDING_BOTTOM);
        
        double cw = Math.max(0, rect.getWidth() - hPad);
        double ch = Math.max(0, rect.getHeight() - vPad);

        if (cw / ch > realW / realH) {
            cw = ch * realW / realH;
        } else {
            ch = cw * realH / realW;
        }

		double x = getContentX(cw);
		double y = getContentY(ch);

		image.draw(g, x, y, cw, ch);
    }
}
