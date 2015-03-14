package com.d_project.xprint.core;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * AbstractXGraphics
 * @author Kazuhiko Arase
 */
public abstract class AbstractXGraphics implements IXGraphics {

    private boolean showBoundingBox;

    private Stroke stroke;
    
    protected AbstractXGraphics() {
    	showBoundingBox = false;
    	stroke = null;
    }
    
    public final void setShowBoundingBox(boolean showBoundingBox) {
    	this.showBoundingBox = showBoundingBox;
    }
    
	public final boolean isShowBoundingBox() {
		return showBoundingBox;
	}

	public final void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public final void draw(Shape shape) {
    	fill(stroke.createStrokedShape(shape) );
	}

	public final void drawText(TextLayout layout, double x, double y) {
		drawText(layout, new AffineTransform(1, 0, 0, 1, x, y) );
	}

	public final void drawText(TextLayout layout, AffineTransform transform) {
		Shape shape = layout.getOutline(transform);
		Rectangle2D rect = shape.getBounds2D();
		if (rect.getWidth() > 0 && rect.getHeight() > 0) {	
			fill(shape);
		}
	}
}
