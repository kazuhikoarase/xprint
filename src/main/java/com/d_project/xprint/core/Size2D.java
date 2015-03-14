package com.d_project.xprint.core;

/**
 * Size2D
 * @author Kazuhiko Arase
 */
public class Size2D extends java.awt.geom.Dimension2D {

    private double width;

    private double height;

    public Size2D() {
        this(0, 0);
    }

    public Size2D(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public String toString() {
        return width + "x" + height;
    }
}
