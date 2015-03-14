package com.d_project.xprint.core;

import java.awt.image.BufferedImage;

/**
 * BitmapImage
 * @author Kazuhiko Arase
 */
public class BitmapImage implements IDrawableImage {
	
	private BufferedImage image;
	
	public BitmapImage(BufferedImage image) {
		this.image = image;
	}
	
	public void draw(IXGraphics g, double x, double y, double width, double height) {
		g.drawImage(image, x, y, x + width, y + height);
	}
	
	public double getWidth() {
		return image.getWidth();
	}

	public double getHeight() {
		return image.getHeight();
	}
}
