package com.d_project.xprint.scripting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import xprint.scripting.IXPrintContext;
import xprint.scripting.IXPrintGraphics;

import com.d_project.xprint.core.FontManager;
import com.d_project.xprint.core.IXGraphics;
import com.d_project.xprint.core.Util;
import com.d_project.xprint.core.XNode;

/**
 * XPrintContextImpl
 * @author Kazuhiko Arase
 */
public class XPrintContextImpl implements IXPrintContext {
	
	private XNode xnode;

	private IXPrintGraphics graphics;

	public XPrintContextImpl(XNode xnode, IXGraphics graphics) {
		this.xnode = xnode;
		this.graphics = (graphics != null)? new XPrintGraphicsImpl(graphics) : null;
	}
	
	public Rectangle2D getBounds() {
		return xnode.getBounds();
	}
	
	public IXPrintGraphics getGraphics() {
		return graphics;
	}
	
	public Color parseColor(String sColor) {
		return Util.parseColor(sColor);
	}
	
	public Font getFont(String family) {
		return FontManager.getInstance().getFont(family);
	}
	
	public double parseNumber(String sNumber) {
		return Util.parseNumber(sNumber);
	}
	
	public Stroke createStroke(String sNumber) {
		return xnode.createStroke(parseNumber(sNumber) );
	}
	
	public Font getNodeFont() {
		return xnode.getFont();
	}
	
	public String getNodeText() {
		return xnode.getText();
	}
}