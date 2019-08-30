package com.d_project.xprint.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.d_project.xprint.core.AttributeKeys;
import com.d_project.xprint.core.AttributeValues;
import com.d_project.xprint.core.NodeNames;
import com.d_project.xprint.core.Util;
import com.d_project.xprint.core.XBarcode;
import com.d_project.xprint.core.XDynamicNode;
import com.d_project.xprint.core.XImage;
import com.d_project.xprint.core.XNode;
import com.d_project.xprint.core.XQRCode;
import com.d_project.xprint.core.XScript;
import com.d_project.xprint.core.XText;

/**
 * XNodeLoader
 * @author Kazuhiko Arase
 */
public class XNodeLoader {

	private XNodeLoader() {
    }
    
    public static XNode load(URL form, URL data) throws Exception {

        if (data != null) {
        	InputStream dataIn = data.openStream();
        	try {
        		return load(form, new StreamSource(dataIn) );
        	} finally {
        		dataIn.close();
        	}
        } else {
            return load(form, (Source)null);
        }
    }
    
    public static XNode load(URL form, Source dataSource) throws Exception {

    	InputStream formIn = form.openStream();
    	try {
    		return load(new StreamSource(formIn), dataSource);
    	} finally {
    		formIn.close();
    	}
    }        

    public static XNode load(Source formSource, Source dataSource) throws Exception {

    	Transformer tr = TransformerFactory.newInstance()
            .newTransformer(formSource);

		if (dataSource == null) {
			dataSource = new DOMSource(createDefaultDocument() );
		}
		
        DOMResult result = new DOMResult();
        tr.transform(dataSource, result);

        Element formNode = (Element)result.getNode().getFirstChild();
        return parse(formNode);
    }        

    private static Document createDefaultDocument() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().
			newDocumentBuilder().newDocument();
		doc.appendChild(doc.createElement("root") );
		return doc;
    }
    
    public static XNode parse(Element formNode) throws Exception {

    	if (formNode == null || !NodeNames.PAGE_NODE.equals(formNode.getNodeName() ) ) {
            throw new IOException("can't find page node.");
        }

        XNode pageNode = new XDynamicNode(NodeNames.PAGE_NODE);

        // default settings.
        setAttributes(pageNode, formNode, new String[]{"40","30","30","30"});

        pageNode.setAttribute(AttributeKeys.PARAGRAPH, String.valueOf(0) );

        if (pageNode.getAttribute(AttributeKeys.FONT_FAMILY) == null) {
            pageNode.setAttribute(AttributeKeys.FONT_FAMILY, "sans-serif");
        }
        if (pageNode.getAttribute(AttributeKeys.FONT_WEIGHT) == null) {
            pageNode.setAttribute(AttributeKeys.FONT_WEIGHT, "plain");
        }
        if (pageNode.getAttribute(AttributeKeys.FONT_SIZE) == null) {
            pageNode.setAttribute(AttributeKeys.FONT_SIZE, "10");
        }

        parse(pageNode, formNode);
        
        return pageNode;
    }
    
    private static void parse(XNode xnode, Node pageNode) throws Exception {
        
        NodeList childNodes = pageNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {

            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            Element childElement = (Element)childNode;
            
            if (NodeNames.DIV_NODE.equals(childElement.getNodeName() ) ) {

                XNode childXNode = new XDynamicNode(NodeNames.DIV_NODE);
                setAttributes(childXNode, childElement, new String[]{"2","2","2","2"});
                xnode.appendChild(childXNode);

                parse(childXNode, childElement);

            } else if (NodeNames.HEADER_NODE.equals(childElement.getNodeName() ) ) {

                XNode childXNode = new XDynamicNode(NodeNames.HEADER_NODE);
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
                xnode.appendChild(childXNode);

                parse(childXNode, childElement);

            } else if (NodeNames.TEXT_NODE.equals(childElement.getNodeName() ) ) {

                XText childXNode = new XText();
                childXNode.setNode(childElement.cloneNode(true) );
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
		        setAttribute(childXNode, childElement, AttributeKeys.LINE_GAP, null);
                xnode.appendChild(childXNode);

            } else if (NodeNames.IMAGE_NODE.equals(childElement.getNodeName() ) ) {

                XImage childXNode = new XImage();
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
                childXNode.setAttribute(AttributeKeys.SRC,
                		childElement.getAttribute(AttributeKeys.SRC) );
                xnode.appendChild(childXNode);

            } else if (NodeNames.BARCODE_NODE.equals(childElement.getNodeName() ) ) {

                XBarcode childXNode = new XBarcode();
                childXNode.setNode(childElement.cloneNode(true) );
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
		        setAttribute(childXNode, childElement, AttributeKeys.TYPE, null);
		        setAttribute(childXNode, childElement, AttributeKeys.UNIT_WIDTH, "0.75");
                xnode.appendChild(childXNode);

            } else if (NodeNames.QRCODE_NODE.equals(childElement.getNodeName() ) ) {

                XQRCode childXNode = new XQRCode();
                childXNode.setNode(childElement.cloneNode(true) );
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
		        setAttribute(childXNode, childElement, AttributeKeys.TYPE_NUMBER, null);
		        setAttribute(childXNode, childElement, AttributeKeys.ERROR_CORRECTION_LEVEL, null);
		        setAttribute(childXNode, childElement, AttributeKeys.UNIT_WIDTH, "0.8");
                xnode.appendChild(childXNode);

            } else if (NodeNames.SCRIPT_NODE.equals(childElement.getNodeName() ) ) {

                XScript childXNode = new XScript();
                childXNode.setNode(childElement.cloneNode(true) );
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
                childXNode.setAttribute(AttributeKeys.SRC,
                		childElement.getAttribute(AttributeKeys.SRC) );
                xnode.appendChild(childXNode);

            } else if (NodeNames.RECORD_NODE.equals(childElement.getNodeName() ) ) {

                int paragraph = 0;

                Node currNode = childElement;

                while (currNode.getParentNode() != null) {
                    if (NodeNames.RECORD_NODE.equals(currNode.getNodeName() ) ) {
                        paragraph++;
                    }
                    currNode = currNode.getParentNode();
                }
                
                XNode childXNode = new XDynamicNode(NodeNames.RECORD_NODE);
                childXNode.setAttribute(AttributeKeys.PARAGRAPH, String.valueOf(paragraph) );
                setAttributes(childXNode, childElement, new String[]{"0","0","0","0"});
		        setAttribute(childXNode, childElement, AttributeKeys.PAGE_BREAK, AttributeValues.NO);
                parse(childXNode, childElement);
                xnode.appendChild(childXNode);
                
            } else {
                parse(xnode, childElement);
            }
        }
    }

    private static void setAttributes(XNode xnode, Element node, String[] defaultPadding) {

        setAttribute(xnode, node, AttributeKeys.COLOR, null);
        setAttribute(xnode, node, AttributeKeys.BACKGROUND_COLOR, null);

        setAttribute(xnode, node, AttributeKeys.WIDTH, "0");
        setAttribute(xnode, node, AttributeKeys.HEIGHT, "0");
                          
        setAttribute(xnode, node, AttributeKeys.ALIGN, AttributeValues.TOP);
        setAttribute(xnode, node, AttributeKeys.CONTENT_ALIGN, AttributeValues.LEFT);
        setAttribute(xnode, node, AttributeKeys.CONTENT_VERTICAL_ALIGN, AttributeValues.CENTER);

        setAttribute(xnode, node, AttributeKeys.FONT_FAMILY, null);
        setAttribute(xnode, node, AttributeKeys.FONT_WEIGHT, null);
        setAttribute(xnode, node, AttributeKeys.FONT_SIZE, null);

        setAttribute(xnode, node, AttributeKeys.STROKE_CAP, null);
        setAttribute(xnode, node, AttributeKeys.STROKE_JOIN, null);
        setAttribute(xnode, node, AttributeKeys.STROKE_MITER_LIMIT, "10");
        setAttribute(xnode, node, AttributeKeys.STROKE_DASH_PATTERN, null);
        setAttribute(xnode, node, AttributeKeys.STROKE_DASH_PHASE, "0");
        
        setAttribute(xnode, node, AttributeKeys.CORNER_WIDTH, "0");
        setAttribute(xnode, node, AttributeKeys.CORNER_HEIGHT, "0");
        setAttribute(xnode, node, AttributeKeys.CORNER_RADIUS, "0");
        
        
        String margin = getAttribute(node, AttributeKeys.MARGIN, "");
        if (margin.length() == 0) {
            setAttribute(xnode, node, AttributeKeys.MARGIN_TOP,    "0");
            setAttribute(xnode, node, AttributeKeys.MARGIN_RIGHT,  "0");
            setAttribute(xnode, node, AttributeKeys.MARGIN_BOTTOM, "0");
            setAttribute(xnode, node, AttributeKeys.MARGIN_LEFT,   "0");
        } else {
            String[] margins = fillLast(split(margin, 4) );
            xnode.setAttribute(AttributeKeys.MARGIN_TOP,    margins[0]);
            xnode.setAttribute(AttributeKeys.MARGIN_RIGHT,  margins[1]);
            xnode.setAttribute(AttributeKeys.MARGIN_BOTTOM, margins[2]);
            xnode.setAttribute(AttributeKeys.MARGIN_LEFT,   margins[3]);
        }
        
        String padding = getAttribute(node, AttributeKeys.PADDING, "");
        if (padding.length() == 0) {
            setAttribute(xnode, node, AttributeKeys.PADDING_TOP,    defaultPadding[0]);
            setAttribute(xnode, node, AttributeKeys.PADDING_RIGHT,  defaultPadding[1]);
            setAttribute(xnode, node, AttributeKeys.PADDING_BOTTOM, defaultPadding[2]);
            setAttribute(xnode, node, AttributeKeys.PADDING_LEFT,   defaultPadding[3]);
        } else {
            String[] paddings = fillLast(split(padding, 4) );
            xnode.setAttribute(AttributeKeys.PADDING_TOP,    paddings[0]);
            xnode.setAttribute(AttributeKeys.PADDING_RIGHT,  paddings[1]);
            xnode.setAttribute(AttributeKeys.PADDING_BOTTOM, paddings[2]);
            xnode.setAttribute(AttributeKeys.PADDING_LEFT,   paddings[3]);
        }

        setAttribute(xnode, node, AttributeKeys.BORDER_COLOR, null);

        String borderWidth = getAttribute(node, AttributeKeys.BORDER_WIDTH, "");
        if (borderWidth.length() == 0) {
            setAttribute(xnode, node, AttributeKeys.BORDER_WIDTH_TOP,    "0");
            setAttribute(xnode, node, AttributeKeys.BORDER_WIDTH_RIGHT,  "0");
            setAttribute(xnode, node, AttributeKeys.BORDER_WIDTH_BOTTOM, "0");
            setAttribute(xnode, node, AttributeKeys.BORDER_WIDTH_LEFT,   "0");
            setAttribute(xnode, node, AttributeKeys.BORDER_WIDTH_LT_RB,  "0");
            setAttribute(xnode, node, AttributeKeys.BORDER_WIDTH_LB_RT,  "0");
        } else {
            String[] borderWidths = fillExplicit(fillLast(split(borderWidth, 6), 4), "0");
            xnode.setAttribute(AttributeKeys.BORDER_WIDTH_TOP,    borderWidths[0]);
            xnode.setAttribute(AttributeKeys.BORDER_WIDTH_RIGHT,  borderWidths[1]);
            xnode.setAttribute(AttributeKeys.BORDER_WIDTH_BOTTOM, borderWidths[2]);
            xnode.setAttribute(AttributeKeys.BORDER_WIDTH_LEFT,   borderWidths[3]);
            xnode.setAttribute(AttributeKeys.BORDER_WIDTH_LT_RB,  borderWidths[4]);
            xnode.setAttribute(AttributeKeys.BORDER_WIDTH_LB_RT,  borderWidths[5]);
        }
    }
    
    private static String[] split(String value, int length) {
        String[] values = new String[length];
        StringTokenizer st = new StringTokenizer(value);
        for (int i = 0; i < length; i++) {
            values[i] = st.hasMoreTokens()? st.nextToken() : null;
        }
        return values;
    }
    
    private static String[] fillLast(String[] values) {
    	return fillLast(values, values.length);
    }
    
    private static String[] fillLast(String[] values, int length) {
        String lastValue = null;
    	for (int i = 0; i < length; i++) {
    		if (values[i] == null) {
    			if (lastValue == null) {
    				throw new NullPointerException();
    			}
    			values[i] = lastValue;
    		} else {
    			lastValue = values[i];
    		}
    	}
    	return values;
    }

    private static String[] fillExplicit(String[] values, String defaultValue) {
    	return fillExplicit(values, defaultValue, values.length);
    }
    
    private static String[] fillExplicit(String[] values, String defaultValue, int length) {
    	for (int i = 0; i < length; i++) {
    		if (values[i] == null) {
    			values[i] = defaultValue;
    		}
    	}
    	return values;
    }

    private static void setAttribute(XNode xnode, Element node, String name, String defaultValue) {
    	xnode.setAttribute(name, getAttribute(node, name, defaultValue) );
    }

    private static String getAttribute(Element node, String name, String defaultValue) {
		String value = node.getAttribute(name);
		return !Util.isEmpty(value)? value : defaultValue;
    }
}