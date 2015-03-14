package com.d_project.xprint.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class XAWTGraphics extends AbstractXGraphics {
	
	private Graphics2D g;
	
	public XAWTGraphics(Graphics2D g) {
		this.g = g;
	}
	
	public void translate(double x, double y) {
		g.translate(x, y);
	}

	public void fill(Shape s) {
		g.fill(s);
	}

	public void setColor(Color color) {
		g.setColor(color);
	}
	
	public void drawImage(BufferedImage image,
			double dx1, double dy1, double dx2, double dy2) {

		AffineTransform tr = new AffineTransform();
		tr.translate(dx1, dy1);
		tr.scale(
			(dx2 - dx1) / image.getWidth(),
			(dy2 - dy1) / image.getHeight() );
		
    	g.drawImage(image, tr, null);
    }
}

