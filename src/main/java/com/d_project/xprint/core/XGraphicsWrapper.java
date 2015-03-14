package com.d_project.xprint.core;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * XGraphicsWrapper
 * @author Kazuhiko Arase
 */
public class XGraphicsWrapper extends Graphics2D {

	private IXGraphics g;
	private AffineTransform trans;
	private Stroke stroke;
	
	public XGraphicsWrapper(IXGraphics g) {
		this.g = g;
		this.trans = new AffineTransform();
		this.stroke = null;
	}

	public void setColor(Color color) {
		g.setColor(color);
	}
	public Color getColor() {
		return null;
	}

	public void setPaint(Paint paint) {
		if (paint instanceof Color) {
			setColor( (Color)paint);
		}
	}
	public Paint getPaint() {
		return null;
	}

	public void setStroke(Stroke stroke) {
		g.setStroke(stroke);
		this.stroke = stroke;
	}
	public Stroke getStroke() {
		return null;
	}
	
	public void draw(Shape shape) {
    	fill(stroke.createStrokedShape(shape) );
	}

	public void fill(Shape shape) {
		g.fill(getTransform().createTransformedShape(shape) );
	}

	public void translate(int x, int y) {
		translate(x, y);
	}
	public void translate(double x, double y) {
		g.translate(x, y);
	}

	public Graphics create() {
		XGraphicsWrapper g = new XGraphicsWrapper(this.g);
		g.setTransform(getTransform() );
		return g;
	}

	public void dispose() {
	}

	public void drawString(String str, int x, int y) {
	}
	public void drawString(String s, float x, float y) {
	}
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
	}
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
	}


	public void setBackground(Color color) {
	}
	public Color getBackground() {
		return null;
	}

	public void setComposite(Composite comp) {
	}
	public Composite getComposite() {
		return null;
	}

	// hints
	
	public void addRenderingHints(Map hints) {
	}
	public void setRenderingHint(Key hintKey, Object hintValue) {
	}
	public Object getRenderingHint(Key hintKey) {
		return null;
	}
	public void setRenderingHints(Map hints) {
	}
	public RenderingHints getRenderingHints() {
		return null;
	}

	// transforms
	public void setTransform(AffineTransform trans) {
		this.trans = trans;
	}
	public AffineTransform getTransform() {
		return trans;
	}
	public void scale(double sx, double sy) {
		trans.scale(sx, sy);
	}
	public void shear(double shx, double shy) {
		trans.shear(shx, shy);
	}
	public void rotate(double theta) {
		trans.rotate(theta);
	}
	public void rotate(double theta, double x, double y) {
		trans.rotate(theta, x, y);
	}
	public void transform(AffineTransform trans) {
		this.trans.concatenate(trans);
	}

	// drawImages
	
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return false;
	}
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return false;
	}
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		return false;
	}
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		return false;
	}
	public boolean drawImage(Image img, 
			int dx1, int dy1, int dx2, int dy2, 
			int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		return false;
	}
	public boolean drawImage(Image img, 
			int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {
		return false;
	}
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		return false;
	}
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
	}
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
	}
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
	}

	// draws
	
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
	}
	public void drawGlyphVector(GlyphVector glyph, float x, float y) {
	}
	public void drawLine(int x1, int y1, int x2, int y2) {
	}
	public void drawOval(int x, int y, int width, int height) {
	}
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	}
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
	}
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
	}

	// fills
	
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
	}
	public void fillOval(int x, int y, int width, int height) {
	}
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	}
	public void fillRect(int x, int y, int width, int height) {
	}
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
	}

	// clips

	public void clearRect(int x, int y, int width, int height) {
	}
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
	}

	public void clip(Shape shape) {
	}
	public void clipRect(int x, int y, int width, int height) {
	}
	public void setClip(Shape clip) {
	}
	public void setClip(int x, int y, int width, int height) {
	}
	public Shape getClip() {
		return null;
	}
	public Rectangle getClipBounds() {
		return null;
	}

	public void setFont(Font font) {
	}
	public Font getFont() {
		return null;
	}

	public FontMetrics getFontMetrics(Font f) {
		return null;
	}
	public FontRenderContext getFontRenderContext() {
		return null;
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return null;
	}
	
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return false;
	}
	
	public void setPaintMode() {
	}
	public void setXORMode(Color color) {
	}
}