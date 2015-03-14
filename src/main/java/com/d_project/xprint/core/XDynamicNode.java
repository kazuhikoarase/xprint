package com.d_project.xprint.core;

import java.awt.geom.Dimension2D;
    
/**
 * XDynamicNode
 * @author Kazuhiko Arase
 */
public class XDynamicNode extends XNode {
    
    public XDynamicNode(String nodeName) {
        super(nodeName);
    }

    public Dimension2D getContentSize(XNodeContext context) {
        return new Size2D(0, 0);
    }

    public XNode cloneNode() {
        XDynamicNode e = new XDynamicNode(getNodeName() );
        e.setNode(getNode() );
        copyAttributes(e);
        return e;
    }

    public void paint(XNodeContext context, IXGraphics g) {
    }

}
