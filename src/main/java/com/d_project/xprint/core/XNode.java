package com.d_project.xprint.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
    
/**
 * XNode
 * @author Kazuhiko Arase
 */
public abstract class XNode {

	protected static final Logger logger = Logger.getLogger(XNode.class.getName() );

    private double x;
    private double y;
    private double width;
    private double height;

    private Dimension2D prefSize;

    private boolean valid;
    
    private List childElements;
    
    private XNode parentNode;

    private Map attributes;
    private boolean cloned;
    
    
    private String nodeName;
    
    private Node node;
    
    protected XNode(String nodeName) {
        this.nodeName = nodeName;
        this.parentNode = null;
        this.childElements = null;
        this.attributes = null;
        this.cloned = false;
    }


    public final void setNode(Node node) {
        this.node = node;
    }
    
    public final Node getNode() {
        return node;
    }
    
    public XNode cloneNode() {
    	try {
	    	XNode e = (XNode)getClass().newInstance();
	        e.setNode(getNode() );
	        copyAttributes(e);
	        return e;
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    protected void copyAttributes(XNode dst) {
    	if (attributes != null) {
	        cloned = true;
	    	dst.attributes = attributes;
	        dst.cloned = true;
    	}
    }
    
    public abstract Dimension2D getContentSize(XNodeContext context);

    public abstract void paint(XNodeContext context, IXGraphics g);

    public boolean isValid() {
        return valid;
    }

    public void resetLayout() {
		setBounds(0, 0, 0, 0);
        for (int i = 0; i < getLength(); i++) {
            item(i).resetLayout();
        }
    }
    
    public final void layoutAll(XNodeContext context) throws LayoutException {

        layoutAll(context, true);

		while (!valid) {
	        layoutAll(context, false);
		}

    }
    
    private void layoutAll(XNodeContext context, boolean silent) throws LayoutException {

        if (!valid) {

            valid = true;

            layout(context, silent);
                    
            for (int i = 0; i < getLength(); i++) {
                item(i).layoutAll(context, silent);
            }
        }
    }
    
    public void invalidate() {

        valid = false;
		prefSize = null;

        if (parentNode != null) {
        	parentNode.invalidate();
        }
    }
        
    public String getNodeName() {
        return nodeName;
    }

    public void dump() {
        dump(0);
    }

    private void dump(int indent) {

        for (int i = 0; i < indent; i++) {
        	logger.finer(" ");
        }

        if (indent > 0) {
        	logger.finer("+");
        }

        logger.finer(this + "[" + x + "," + y + " " + width + "x" + height + "," + getAttribute("align") + "]");

        for (int i = 0; i < getLength(); i++) {
            item(i).dump(indent + 2);
        }
        
    }
    
    public final XNode cloneNode(boolean deep) {
        
        XNode e = cloneNode();

        if (deep) {
            for (int i = 0; i < getLength(); i++) {
                e.appendChild(item(i).cloneNode(deep) );
            }
        }

        return e;
    }
    
    public XNode getParentNode() {
        return parentNode;
    }
    
    public int getLength() {
        return (childElements != null)? childElements.size() : 0;
    }

    public XNode item(int i) {
        return (XNode)childElements.get(i);
    }

    public void appendChild(XNode c) {

        if (c.parentNode != null) {
            c.parentNode.remove(c);
        }

        if (childElements == null) {
        	childElements = new ArrayList(1);
        }

        childElements.add(c);
        c.parentNode = this;

        invalidate();
    }
  
    public void remove(XNode c) {

        if (childElements != null && childElements.contains(c) ) {
            childElements.remove(c);
            c.parentNode = null;

            invalidate();
        }
    }

    public void removeAll() {
        while (getLength() > 0) {
            remove(item(getLength() - 1) );
        }
    }

    public void setBounds(double x, double y, double width, double height) {

        boolean resized = Util.compare(this.width, width) != 0
        	|| Util.compare(this.height, height) != 0;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        if (resized) {
            invalidate();
        }

    }
    
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public void setAttribute(String name, String value) {

    	if (cloned) {
    		// クローン化されている場合、コピーを作成
    		Map newAttributes = new HashMap();
    		newAttributes.putAll(attributes);
    		attributes = newAttributes;
    		cloned = false;
    	}
    	
    	if (value != null) {
        	if (attributes == null) {
				attributes = new HashMap();
        	}
            attributes.put(name, value);
        } else {
        	if (attributes != null) {
	            attributes.remove(name);
        	}
        }
    }

    public String getAttribute(String name) {
        return (attributes != null)? (String)attributes.get(name) : null;
    }
    
    public String getAttribute(String name, boolean inherit) {
        String value = getAttribute(name);
        if (value == null && inherit && parentNode != null) {
            return parentNode.getAttribute(name, inherit);
        }
        return value;
    }

    public double getNumberAttribute(String name) {
        return Util.parseNumber(getAttribute(name) );
    }
    
    public double getNumberAttribute(String name, boolean inherit) {
        return Util.parseNumber(getAttribute(name, inherit) );
    }
    

    public Font getFont() {
        String family = getAttribute(AttributeKeys.FONT_FAMILY, true);
        float weight = AttributeValues.BOLD.equals(getAttribute(AttributeKeys.FONT_WEIGHT, true) )? 2.0f : 1.0f;
        float size = (float)getNumberAttribute(AttributeKeys.FONT_SIZE, true);
        return FontManager.getInstance().getFont(family, weight, size);
    }

    public final Dimension2D getPreferredSize(XNodeContext context) {
    	if (prefSize == null) {
    		prefSize = getPreferredSizeImpl(context);
    	}
		return prefSize;
    }
    
    private Dimension2D getPreferredSizeImpl(XNodeContext context) {
        
        double prefWidth  = 0;
        double prefHeight = 0;
        
        double sumWidth  = 0;
        double sumHeight = 0;
        
        double centerWidth  = 0;
        double centerHeight = 0;
        
        for (int i = 0; i < getLength(); i++) {

            XNode c = item(i);
            Dimension2D prefSize = c.getPreferredSize(context);

            String align = c.getAttribute(AttributeKeys.ALIGN);
            align = safeAlign(align);

            double marginLeft   = c.getNumberAttribute(AttributeKeys.MARGIN_LEFT);
            double marginRight  = c.getNumberAttribute(AttributeKeys.MARGIN_RIGHT);
            double marginTop    = c.getNumberAttribute(AttributeKeys.MARGIN_TOP);
            double marginBottom = c.getNumberAttribute(AttributeKeys.MARGIN_BOTTOM);
            
            double cPrefWidth = prefSize.getWidth() + marginLeft + marginRight;
            double cPrefHeight = prefSize.getHeight() + marginTop + marginBottom;

            if (AttributeValues.LEFT.equals(align) ) {

                prefHeight = Math.max(prefHeight, cPrefHeight);
                sumWidth += cPrefWidth;

            } else if (AttributeValues.RIGHT.equals(align) ) {

                prefHeight = Math.max(prefHeight, cPrefHeight);
                sumWidth += cPrefWidth;

            } else if (AttributeValues.TOP.equals(align) ) {

                prefWidth = Math.max(prefWidth, cPrefWidth);
                sumHeight += cPrefHeight;

            } else if (AttributeValues.BOTTOM.equals(align) ) {

                prefWidth = Math.max(prefWidth, cPrefWidth);
                sumHeight += cPrefHeight;

            } else if (AttributeValues.CENTER.equals(align) ) {

                centerWidth = Math.max(centerWidth, cPrefWidth);
                centerHeight = Math.max(centerHeight, cPrefHeight);
            }
        }
        
        sumWidth += centerWidth;
        prefHeight = Math.max(prefHeight, centerHeight);

        prefWidth = Math.max(prefWidth, sumWidth);
        prefHeight += sumHeight;

        Dimension2D contSize = getContentSize(context);
        prefWidth  = Math.max(prefWidth, contSize.getWidth() );
        prefHeight = Math.max(prefHeight, contSize.getHeight() );

        prefWidth  += (getNumberAttribute(AttributeKeys.PADDING_LEFT)
            + getNumberAttribute(AttributeKeys.PADDING_RIGHT) );
        prefHeight += (getNumberAttribute(AttributeKeys.PADDING_TOP)
            + getNumberAttribute(AttributeKeys.PADDING_BOTTOM) );

        double fixedSizeWidth  = getNumberAttribute(AttributeKeys.WIDTH);
        double fixedSizeHeight = getNumberAttribute(AttributeKeys.HEIGHT);
        
        if (fixedSizeWidth > 0) {
            prefWidth = fixedSizeWidth;
        }

        if (fixedSizeHeight > 0) {
            prefHeight = fixedSizeHeight;        
        }

        return new Size2D(prefWidth, prefHeight);
    }

    private void layout(XNodeContext context, boolean silent) throws LayoutException {

        double xMin = getNumberAttribute(AttributeKeys.PADDING_LEFT);
        double xMax = this.width - getNumberAttribute(AttributeKeys.PADDING_RIGHT);

        double yMin = getNumberAttribute(AttributeKeys.PADDING_TOP);
        double yMax = this.height - getNumberAttribute(AttributeKeys.PADDING_BOTTOM);

        //--------------------------------------
        // TOP
        //--------------------------------------

        for (int i = 0; i < getLength(); i++) {

            XNode node = item(i);

            String align = node.getAttribute(AttributeKeys.ALIGN);
            align = safeAlign(align);

            if (AttributeValues.TOP.equals(align) ) {

                double marginLeft   = node.getNumberAttribute(AttributeKeys.MARGIN_LEFT);
                double marginRight  = node.getNumberAttribute(AttributeKeys.MARGIN_RIGHT);
                double marginTop    = node.getNumberAttribute(AttributeKeys.MARGIN_TOP);
                double marginBottom = node.getNumberAttribute(AttributeKeys.MARGIN_BOTTOM);

                Dimension2D size = node.getPreferredSize(context);

                double prefH = size.getHeight() + marginTop + marginBottom;
                double realH = yMax - yMin;

                double w = xMax - xMin;
                double h = Math.max(0, Math.min(realH, prefH) );
                
                double tw = Math.max(0, w - (marginLeft + marginRight) );
                double th = Math.max(0, h - (marginTop + marginBottom) );
                
                node.setBounds(xMin + marginLeft, yMin + marginTop, tw, th);
                if (!silent) node.checkLayout(context);
                yMin += h;
            }
        }

        //--------------------------------------
        // BOTTOM
        //--------------------------------------

        for (int i = getLength() - 1; i >= 0; i--) {

            XNode node = item(i);

            String align = node.getAttribute(AttributeKeys.ALIGN);
            align = safeAlign(align);

            if (AttributeValues.BOTTOM.equals(align) ) {

                double marginLeft   = node.getNumberAttribute(AttributeKeys.MARGIN_LEFT);
                double marginRight  = node.getNumberAttribute(AttributeKeys.MARGIN_RIGHT);
                double marginTop    = node.getNumberAttribute(AttributeKeys.MARGIN_TOP);
                double marginBottom = node.getNumberAttribute(AttributeKeys.MARGIN_BOTTOM);

                Dimension2D size = node.getPreferredSize(context);

                double prefH = size.getHeight() + marginTop + marginBottom;
                double realH = yMax - yMin;

                double w = xMax - xMin;
                double h = Math.min(realH, prefH);

                double tw = Math.max(0, w - (marginLeft + marginRight) );
                double th = Math.max(0, h - (marginTop + marginBottom) );

                node.setBounds(xMin + marginLeft, yMax - h + marginTop, tw, th);
                if (!silent) node.checkLayout(context);
                yMax -= h;
            }

        }

        //--------------------------------------
        // LEFT
        //--------------------------------------

        for (int i = 0; i < getLength(); i++) {

            XNode node = item(i);

            String align = node.getAttribute(AttributeKeys.ALIGN);
            align = safeAlign(align);

            if (AttributeValues.LEFT.equals(align) ) {

                double marginLeft   = node.getNumberAttribute(AttributeKeys.MARGIN_LEFT);
                double marginRight  = node.getNumberAttribute(AttributeKeys.MARGIN_RIGHT);
                double marginTop    = node.getNumberAttribute(AttributeKeys.MARGIN_TOP);
                double marginBottom = node.getNumberAttribute(AttributeKeys.MARGIN_BOTTOM);

                Dimension2D size = node.getPreferredSize(context);

                double prefW = size.getWidth() + marginLeft + marginRight;
                double realW = xMax - xMin;

                double w = Math.min(realW, prefW);
                double h = yMax - yMin;

                double tw = Math.max(0, w - (marginLeft + marginRight) );
                double th = Math.max(0, h - (marginTop + marginBottom) );

                node.setBounds(xMin + marginLeft, yMin + marginTop, tw, th);
                if (!silent) node.checkLayout(context);
                xMin += w;
            }
        }

        //--------------------------------------
        // RIGHT
        //--------------------------------------

        for (int i = getLength() - 1; i >= 0; i--) {

            XNode node = item(i);

            String align = node.getAttribute(AttributeKeys.ALIGN);
            align = safeAlign(align);

            if (AttributeValues.RIGHT.equals(align) ) {

                double marginLeft   = node.getNumberAttribute(AttributeKeys.MARGIN_LEFT);
                double marginRight  = node.getNumberAttribute(AttributeKeys.MARGIN_RIGHT);
                double marginTop    = node.getNumberAttribute(AttributeKeys.MARGIN_TOP);
                double marginBottom = node.getNumberAttribute(AttributeKeys.MARGIN_BOTTOM);

                Dimension2D size = node.getPreferredSize(context);

                double prefW = size.getWidth() + marginLeft + marginRight;
                double realW = xMax - xMin;

                double w = Math.min(realW, prefW);
                double h = yMax - yMin;

                double tw = Math.max(0, w - (marginLeft + marginRight) );
                double th = Math.max(0, h - (marginTop + marginBottom) );

                node.setBounds(xMax - w + marginLeft, yMin + marginTop, tw, th);
                if (!silent) node.checkLayout(context);
                xMax -= w;
            }
        }

        //--------------------------------------
        // CENTER
        //--------------------------------------

        for (int i = 0; i < getLength(); i++) {

            XNode node = item(i);

            String align = node.getAttribute(AttributeKeys.ALIGN);
            align = safeAlign(align);

            if (AttributeValues.CENTER.equals(align) ) {

                double marginLeft   = node.getNumberAttribute(AttributeKeys.MARGIN_LEFT);
                double marginRight  = node.getNumberAttribute(AttributeKeys.MARGIN_RIGHT);
                double marginTop    = node.getNumberAttribute(AttributeKeys.MARGIN_TOP);
                double marginBottom = node.getNumberAttribute(AttributeKeys.MARGIN_BOTTOM);

                double w = xMax - xMin;
                double h = yMax - yMin;

                double tw = Math.max(0, w - (marginLeft + marginRight) );
                double th = Math.max(0, h - (marginTop + marginBottom) );

                node.setBounds(xMin + marginLeft, yMin + marginTop, tw, th);
                if (!silent) node.checkLayout(context);
            }
        }
    }

    private void checkLayout(XNodeContext context) throws LayoutException {

        Dimension2D size = getPreferredSize(context);
        Rectangle2D rect = getBounds();

        if (Util.compare(size.getWidth(), rect.getWidth() ) > 0 ||
        		Util.compare(size.getHeight(), rect.getHeight() ) > 0) {

            String msg = this.toString()
                + " - pref > rect = " 
                + size.getWidth() + "x" + size.getHeight()
                + " > "
                + rect.getWidth() + "x" + rect.getHeight();
            throw new LayoutException(msg);
        }
    }
        
    private static String safeAlign(String align) {

        if (AttributeValues.BOTTOM.equals(align) ) {
            return align;
        } else if (AttributeValues.LEFT.equals(align) ) {
            return align;
        } else if (AttributeValues.RIGHT.equals(align) ) {
            return align;
        } else if (AttributeValues.CENTER.equals(align) ) {
            return align;
        }
        return AttributeValues.TOP;
    }

    public final void paintAll(XNodeContext context, IXGraphics g) {

        g.translate(x, y);
                
        paintBackground(g);        

        // 前景色、背景色を設定して呼び出し。
        String color = getAttribute(AttributeKeys.COLOR);
        //g.setFont(getFont() );
        g.setColor(Util.parseColor(color) );

        paint(context, g);

        for (int i = 0; i < getLength(); i++) {
            item(i).paintAll(context, g);
        }

        paintForeground(g);        

        g.translate(-x, -y);

    }
    
    public double getContentX(double cw) {

    	double left = getNumberAttribute(AttributeKeys.PADDING_LEFT);
		double right = getNumberAttribute(AttributeKeys.PADDING_RIGHT);
        Rectangle2D rect = getBounds();

        double x = 0;

        if (AttributeValues.LEFT.equals(getAttribute(AttributeKeys.CONTENT_ALIGN) ) ) {
            x = left;
        } else if (AttributeValues.RIGHT.equals(getAttribute(AttributeKeys.CONTENT_ALIGN) ) ) {
            x = rect.getWidth() - right - cw;
        } else {
            x = (rect.getWidth() - left - right - cw) / 2 + left;
        }
        
        return x;
    }

    public double getContentY(double ch) {

    	double top = getNumberAttribute(AttributeKeys.PADDING_TOP);
		double bottom = getNumberAttribute(AttributeKeys.PADDING_BOTTOM);
        Rectangle2D rect = getBounds();

        double y = 0;
                
        if (AttributeValues.TOP.equals(getAttribute(AttributeKeys.CONTENT_VERTICAL_ALIGN) ) ) {
            y = top;
        } else if (AttributeValues.BOTTOM.equals(getAttribute(AttributeKeys.CONTENT_VERTICAL_ALIGN) ) ) {
            y = rect.getHeight() - bottom - ch;
        } else {
            y = (rect.getHeight() - top - bottom - ch) / 2 + top;
        }
        
        return y;
    }

    public String getText() {
        StringBuffer buffer = new StringBuffer();
        createAllText(buffer);
        return buffer.toString();
    }
    
    private void createAllText(StringBuffer buffer) {
    	createText(buffer);
    	for (int i = 0; i < getLength(); i++) {
    		item(i).createAllText(buffer);
    	}
    }
    
    private void createText(StringBuffer buffer) {
    	
    	if (getNode() == null) {
    		return;
    	}
    	
        NodeList childs = getNode().getChildNodes();

        for (int i = 0; i < childs.getLength(); i++) {

            Node childNode = childs.item(i);

            if (childNode.getNodeType() == Node.TEXT_NODE) {
 
                buffer.append(childNode.getNodeValue() );
 
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                if (NodeNames.PROPERTY_NODE.equals(childNode.getNodeName() ) ) {

					String attrName = childNode.getAttributes()
					    .getNamedItem(AttributeKeys.SELECT).getNodeValue();
					String text = getAttribute(attrName, true);
 
					if (text != null) {
					    buffer.append(text);
					}

                } else {
                	throw new IllegalStateException("invalid node:" + childNode.getNodeName() );
                }
            }
        }
    }
    
    public String toString() {
        return getNodeName() /*+ "#" + id*/;
    }
	    
    private void paintForeground(IXGraphics g) {
		if (g.isShowBoundingBox() ) {
	        g.setColor(Color.blue);
	        g.setStroke(new BasicStroke(0.5f) );
	        g.draw(new Rectangle2D.Double(0, 0, width, height) );
		}
    }
    
    private void paintBackground(IXGraphics g) {

        String backgroundColor = getAttribute(AttributeKeys.BACKGROUND_COLOR);

        double cornerWidth  = getNumberAttribute(AttributeKeys.CORNER_WIDTH);
        double cornerHeight = getNumberAttribute(AttributeKeys.CORNER_HEIGHT);
        double cornerRadius = getNumberAttribute(AttributeKeys.CORNER_RADIUS);
        
        if (cornerRadius > 0) {
        	// cornerRadius を優先する。
        	cornerWidth = cornerRadius;
        	cornerHeight = cornerRadius;
        }

        double borderWidthRight  = getNumberAttribute(AttributeKeys.BORDER_WIDTH_RIGHT);
        double borderWidthLeft   = getNumberAttribute(AttributeKeys.BORDER_WIDTH_LEFT);
        double borderWidthTop    = getNumberAttribute(AttributeKeys.BORDER_WIDTH_TOP);
        double borderWidthBottom = getNumberAttribute(AttributeKeys.BORDER_WIDTH_BOTTOM);

        // 背景
        if (backgroundColor != null) {
            
        	g.setColor(Util.parseColor(backgroundColor) );

            if (cornerWidth > 0 && cornerHeight > 0) {
                g.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerWidth, cornerHeight) );
            } else {
	            g.fill(new Rectangle2D.Double(0, 0, width, height) );
            }
        }

        // 枠線
        String color = getAttribute(AttributeKeys.BORDER_COLOR);
        if (color == null) {
        	// 枠線の色が未指定の場合、テキストの色。
        	color = getAttribute(AttributeKeys.COLOR);
        }
        g.setColor(Util.parseColor(color) );


        if (borderWidthTop > 0 
                && Util.compare(borderWidthTop, borderWidthRight) == 0
                && Util.compare(borderWidthTop, borderWidthBottom) == 0
                && Util.compare(borderWidthTop, borderWidthLeft) == 0) {

            
            g.setStroke(createStroke(borderWidthTop) );

            if (cornerWidth > 0 && cornerHeight > 0) {
                g.draw(new RoundRectangle2D.Double(0, 0, width, height, cornerWidth, cornerHeight) );
            } else {
	            g.draw(new Rectangle2D.Double(0, 0, width, height) );
            }

        } else {
                             
            if (borderWidthTop > 0) {
                g.setStroke(createStroke(borderWidthTop) );
                g.draw(new Line2D.Double(0, 0, width, 0) );
            }
    
            if (borderWidthRight > 0) {
                g.setStroke(createStroke(borderWidthRight) );
                g.draw(new Line2D.Double(width, 0, width, height) );
            }
    
            if (borderWidthBottom > 0) {
                g.setStroke(createStroke(borderWidthBottom) );
                g.draw(new Line2D.Double(0, height, width, height) );
            }
    
            if (borderWidthLeft > 0) {
                g.setStroke(createStroke(borderWidthLeft) );
                g.draw(new Line2D.Double(0, 0, 0, height) );
            }
        }                

        
        // 対角線
        
        double borderWidthLtRb = getNumberAttribute(AttributeKeys.BORDER_WIDTH_LT_RB);
        double borderWidthLbRt = getNumberAttribute(AttributeKeys.BORDER_WIDTH_LB_RT);

        if (borderWidthLtRb > 0) {
            g.setStroke(createStroke(borderWidthLtRb) );
            g.draw(new Line2D.Double(0, 0, width, height) );
        }

        if (borderWidthLbRt > 0) {
            g.setStroke(createStroke(borderWidthLbRt) );
            g.draw(new Line2D.Double(0, height, width, 0) );
        }
    }

    public Stroke createStroke(double width) {
    
    	String strokeCap = getAttribute(AttributeKeys.STROKE_CAP);
        int cap;
        if (AttributeValues.BUTT.equals(strokeCap) ) {
        	cap = BasicStroke.CAP_BUTT;
        } else if (AttributeValues.ROUND.equals(strokeCap) ) {
        	cap = BasicStroke.CAP_ROUND;
        } else if (AttributeValues.SQUARE.equals(strokeCap) ) {
        	cap = BasicStroke.CAP_SQUARE;
        } else{
        	cap = BasicStroke.CAP_SQUARE;
        }

        String strokeJoin = getAttribute(AttributeKeys.STROKE_JOIN);
        int join;
        if (AttributeValues.ROUND.equals(strokeJoin) ) {
        	join = BasicStroke.JOIN_ROUND;
        } else if (AttributeValues.BEVEL.equals(strokeJoin) ) {
        	join = BasicStroke.JOIN_BEVEL;
        } else if (AttributeValues.MITER.equals(strokeJoin) ) {
        	join = BasicStroke.JOIN_MITER;
        } else {
        	join = BasicStroke.JOIN_MITER;
        }

        double miterLimit = getNumberAttribute(AttributeKeys.STROKE_MITER_LIMIT);
        miterLimit = Math.max(1, miterLimit);
        
        String dashPattern = getAttribute(AttributeKeys.STROKE_DASH_PATTERN);

        if (dashPattern != null) {
	        
        	double dashPhase = getNumberAttribute(AttributeKeys.STROKE_DASH_PHASE);
        	String[] patternList = dashPattern.split("\\s+");
	        float[] dash = new float[patternList.length];
	        for (int i = 0; i < patternList.length; i++) {
	        	dash[i] = (float)Util.parseNumber(patternList[i]);
	        }
	        
	        return new BasicStroke( (float)width, cap, join, (float)miterLimit, dash, (float)dashPhase);
        } else {
	        return new BasicStroke( (float)width, cap, join, (float)miterLimit);
        }
    }
}
