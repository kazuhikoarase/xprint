package com.d_project.xprint.core;

/**
 * IDrawableImage
 * @author Kazuhiko Arase
 */
public interface IDrawableImage {
	double getWidth();
	double getHeight();
	void draw(IXGraphics g, double x, double y, double width, double height);
}
