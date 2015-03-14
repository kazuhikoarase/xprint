package com.d_project.xprint.scripting;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import xprint.scripting.IXPrintGraphics;

import com.d_project.xprint.core.IXGraphics;

/**
 * XPrintGraphicsImpl
 * @author Kazuhiko Arase
 */
public class XPrintGraphicsImpl implements IXPrintGraphics {
	
	private IXGraphics g;
	
	public XPrintGraphicsImpl(IXGraphics g) {
		this.g = g;
	}

	public void draw(Shape s) {
		g.draw(s);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2) {
		draw(new Line2D.Double(x1, y1, x2, y2) );
	}
	
	public void drawImage(BufferedImage image, double dx1, double dy1, double dx2, double dy2) {
		g.drawImage(image, dx1, dy1, dx2, dy2);
	}

	public void drawText(TextLayout layout, double x, double y) {
		g.drawText(layout, x, y);
	}

	public void drawText(TextLayout layout, AffineTransform transform) {
		g.drawText(layout, transform);
	}
	
	public void fill(Shape s) {
		g.fill(s);
	}
	
	public void setColor(Color color) {
		g.setColor(color);
	}

	public void setStroke(Stroke stroke) {
		g.setStroke(stroke);
	}
}