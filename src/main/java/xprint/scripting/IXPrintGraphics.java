package xprint.scripting;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public interface IXPrintGraphics {

	void draw(Shape s);

	void fill(Shape s);

	void setColor(Color color);

	void setStroke(Stroke stroke);

	void drawLine(double x1, double y1, double x2, double y2);

	void drawText(TextLayout layout, double x, double y);

	void drawText(TextLayout layout, AffineTransform transform);

	void drawImage(BufferedImage image,
			double dx1, double dy1, double dx2, double dy2);	
}