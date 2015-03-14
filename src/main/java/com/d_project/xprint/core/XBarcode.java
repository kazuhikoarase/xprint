package com.d_project.xprint.core;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import com.d_project.xprint.barcode.Barcode;

/**
 * XBarcode
 * @author Kazuhiko Arase
 */
public class XBarcode extends XNode {

    private Barcode barcode = null;
    
    public XBarcode() {
        super(NodeNames.BARCODE_NODE);
    }

    public Barcode getBarcode() {
        if (barcode == null) {
            barcode = Barcode.newInstance(getAttribute(AttributeKeys.TYPE) );
        }
        return barcode;
    }
    
    public Dimension2D getContentSize(XNodeContext context) {

        Barcode barcode = getBarcode();

        barcode.setData(getText() );
        double narrowBarWidth = getNumberAttribute(AttributeKeys.UNIT_WIDTH);
        
        return new Size2D(barcode.getPatternLength() * narrowBarWidth, 18);
    }
    
    public void paint(XNodeContext context, IXGraphics g) {

        Barcode barcode = getBarcode();
        barcode.setData(getText() );

        Rectangle2D rect = getBounds();
        Double[] pattern = barcode.getPattern();

        double unitWidth = getNumberAttribute(AttributeKeys.UNIT_WIDTH);

/*
        double x = 0;
        double y = 0;
        double bw = barcode.getPatternLength() * narrowBarWidth;
        double bh = rect.getHeight();

        if (AttributeValues.TOP.equals(getAttribute(AttributeKeys.CONTENT_VERTICAL_ALIGN) ) ) {
            y = getDoubleAttribute(AttributeKeys.PADDING_TOP);
        } else if (AttributeValues.BOTTOM.equals(getAttribute(AttributeKeys.CONTENT_VERTICAL_ALIGN) ) ) {
            y = rect.getHeight() - getDoubleAttribute(AttributeKeys.PADDING_BOTTOM) - bh;
        } else {
            y = (rect.getHeight() - bh) / 2;
        }

        if (AttributeValues.LEFT.equals(getAttribute(AttributeKeys.CONTENT_ALIGN) ) ) {
            x = getDoubleAttribute(AttributeKeys.PADDING_LEFT);
        } else if (AttributeValues.RIGHT.equals(getAttribute(AttributeKeys.CONTENT_ALIGN) ) ) {
            x = rect.getWidth() - getDoubleAttribute(AttributeKeys.PADDING_RIGHT) - bw;
        } else {
            x = (rect.getWidth() - bw) / 2;
        }
*/
        double cw = barcode.getPatternLength() * unitWidth;
        
        double ch = Math.max(0, rect.getHeight()
        	 - getNumberAttribute(AttributeKeys.PADDING_TOP)
        	 - getNumberAttribute(AttributeKeys.PADDING_BOTTOM) );

		double x = getContentX(cw);
		double y = getContentY(ch);
		
        for (int i = 0; i < pattern.length; i++) {

            double width = pattern[i].doubleValue() * unitWidth;
            double height = ch;

            if (i % 2 == 1) {
                g.fill(new Rectangle2D.Double(x, y, width, height) );
            }

            x += width;
        }
        
    }

}
