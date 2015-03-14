package com.d_project.xprint.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

/**
 * GraphicsNodeImage
 * @author Kazuhiko Arase
 */
public class GraphicsNodeImage implements IDrawableImage {
    
	public static IDrawableImage read(InputStream in) 
	throws Exception {
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(
				XMLResourceDescriptor.getXMLParserClassName(), false);
		SVGDocument document = (SVGDocument)factory.createDocument(null, in);
		BridgeContext bridgeContext = new BridgeContext(new UserAgentAdapter() );
		GVTBuilder builder = new GVTBuilder();
		GraphicsNode gnode = builder.build(bridgeContext, document);
    	return new GraphicsNodeImage(gnode.getRoot() );
    }
    
	private GraphicsNode rootNode;
	private Rectangle2D bounds;

	public GraphicsNodeImage(GraphicsNode rootNode) {
		this.rootNode = rootNode;
		this.bounds = rootNode.getBounds();
	}
	
	public void draw(IXGraphics g, double x, double y, double width, double height) {
        Graphics2D g2d = new XGraphicsWrapper(g);
        AffineTransform tr = new AffineTransform();
        tr.translate(x, y);
        tr.scale(width / getWidth(), height / getHeight() );
        rootNode.setTransform(tr);
    	rootNode.paint(g2d);
	}
	
	public double getWidth() {
		return bounds.getWidth();
	}

	public double getHeight() {
		return bounds.getHeight();
	}
}
