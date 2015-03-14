package com.d_project.xprint.core;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * IXGraphics
 * @author Kazuhiko Arase
 */
public interface IXGraphics {

    void setShowBoundingBox(boolean showBoundingBox);

	boolean isShowBoundingBox();

	void translate(double x, double y);

	void draw(Shape shape);

	void fill(Shape shape);

	void setColor(Color color);

	void setStroke(Stroke stroke);

	void drawText(TextLayout layout, double x, double y);

	void drawText(TextLayout layout, AffineTransform transform);
	
	void drawImage(BufferedImage image,
			double dx1, double dy1, double dx2, double dy2);
}
