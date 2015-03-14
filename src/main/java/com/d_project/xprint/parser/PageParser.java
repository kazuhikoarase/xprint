package com.d_project.xprint.parser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.d_project.xprint.core.AttributeKeys;
import com.d_project.xprint.core.AttributeValues;
import com.d_project.xprint.core.LayoutException;
import com.d_project.xprint.core.NodeNames;
import com.d_project.xprint.core.XNode;
import com.d_project.xprint.core.XNodeContext;

/**
 * PageParser
 * @author Kazuhiko Arase
 */
public class PageParser {
	
	private final Logger logger = Logger.getLogger(getClass().getName() );

	private XNodeContext context;

	private XNode documentNode;
    private List pageList;

    private XNode parentNode;
    private XNode pageNode;
    private int paragraph;

    private IPageParserListener listener;

    public PageParser(XNodeContext context, XNode documentNode) {
    	this.context = context;
        this.documentNode = documentNode;
        this.pageList = new ArrayList();
    }
    
    public void setPageParserListener(IPageParserListener listener) {
        this.listener = listener;
    }

    public void createPages() throws LayoutException {

		long startTime = System.currentTimeMillis();

        fireBeginEvent();

        try {

            pageList.clear();
            
            XNode currentNode = documentNode;
    
            newPage(currentNode);        

            createPagesImpl(context, currentNode);

            // ページ番号付与
            for (int i = 0; i < getPageCount(); i++) {
                getPage(i).setAttribute(AttributeKeys.PAGE_NO,
                    String.valueOf(i + 1) );
                getPage(i).setAttribute(AttributeKeys.PAGE_COUNT,
                    String.valueOf(getPageCount() ) );
            }

            // 再レイアウト
            for (int i = 0; i < getPageCount(); i++) {
	            getPage(i).layoutAll(context);
            }

        } finally {
            
        	fireEndEvent();

            long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			long freeMemory = Runtime.getRuntime().freeMemory();
			long totalMemory = Runtime.getRuntime().totalMemory();

			logger.finer(
				MessageFormat.format(
					"parse {0} page(s) in {1} millis, current memory: {2}/{3}bytes(free/total).",
					new Object[]{
							new Integer(getPageCount() ),
							new Long(totalTime),
							new Long(freeMemory),
							new Long(totalMemory)
					} 
				)
			);
        }
    }
    
    private void newPage(XNode currentNode) {

        fireNewPageEvent();

        double pageWidth = documentNode.getNumberAttribute(AttributeKeys.WIDTH);
        double pageHeight = documentNode.getNumberAttribute(AttributeKeys.HEIGHT);

        paragraph = 0;

        pageNode = documentNode.cloneNode();
        pageNode.setBounds(0, 0, pageWidth, pageHeight);

        parentNode = pageNode;
        pageList.add(pageNode);

        // ヘッダーを追加する。
        addHeader(currentNode);
    }

    private void createPagesImpl(
    		XNodeContext context, XNode currentNode) {

        for (int i = 0; i < currentNode.getLength(); i++) {

            XNode childNode = currentNode.item(i);        
    
            XNode newNode = childNode.cloneNode(true);
            
            boolean deepCopy;
    
            if (isParagraphNode(childNode) ) {
                deepCopy = false;
            } else {
                deepCopy = true;
            }
            
            int paragraph;
            
            try {
            	paragraph = Integer.parseInt(
            		newNode.getAttribute(AttributeKeys.PARAGRAPH, true) );
            } catch(Exception e) {
            	paragraph = 0;
            }
            
            // ページ変更フラグ
            boolean pageChanged = false;

            // 改ページ指定            
            String pageBreak = childNode.getAttribute(AttributeKeys.PAGE_BREAK);

            if (AttributeValues.ALWAYS.equals(pageBreak) ) {
    
                // 常に改ページ
                if (paragraph <= this.paragraph) {
                    newPage(childNode);
                    pageChanged = true;
                }
                
            } else if (AttributeValues.AUTO.equals(pageBreak) ) {
    
                if (paragraph <= this.paragraph && !isInPage(context, newNode) ) {
                    // 現在のページに収まらず、
                    // 自分と同等以上のパラグラフが含まれている場合は改ページ
                    newPage(childNode);
                    pageChanged = true;
                }

            }
            
            if (!deepCopy) {
            	newNode.removeAll();
            }

            if (!isInPage(context, newNode) ) {
    
                if (pageChanged) {
                    // 既に改ページ済み
                    throw new RuntimeException("give up. (phase1)");
                }

			    logger.finer("page-break:" + getPageCount() 
				    + "/" + parentNode.getNodeName()
				    + "/" + parentNode.getLength() );

                newPage(childNode);
                
                if (!isInPage(context, newNode) ) {
                    throw new RuntimeException("give up. (phase2)");
                }
            }
    
            parentNode.appendChild(newNode);
            
            if (isParagraphNode(childNode) ) {
                this.paragraph = paragraph;
            }
                        
            if (!deepCopy) {
    
                // 浅いコピーの場合はループする。

                parentNode = newNode;                        
    
                createPagesImpl(context, childNode);

                parentNode = parentNode.getParentNode();
            }
        }
    } 

    private boolean isInPage(XNodeContext context, XNode node) {

		node.resetLayout();
        
        parentNode.appendChild(node);

        boolean inPage = true;

        try {
            pageNode.layoutAll(context);
        } catch(LayoutException e) {
            inPage = false;
        }

        parentNode.remove(node);

        return inPage;
    }

    private void addHeader(XNode currentNode) {
        if (currentNode.getParentNode() != null) {
            addHeaderImpl(currentNode.getParentNode(), currentNode, currentNode);
        }
    }

    private void addHeaderImpl(XNode parentNode, XNode pathNode, XNode currentNode) {

        if (parentNode.getParentNode() != null) {
            addHeaderImpl(parentNode.getParentNode(), parentNode, currentNode);
        }
		
        for (int i = 0; i < parentNode.getLength(); i++) {
			
			XNode childNode = parentNode.item(i);
			
            if (childNode == pathNode) {

            	if (childNode != currentNode) {
			    	XNode newParentNode = childNode.cloneNode(false);
			    	this.parentNode.appendChild(newParentNode);
			    	this.parentNode = newParentNode;
            	}

                break;
            }

            if (childNode.getNodeName().equals(NodeNames.HEADER_NODE) ) {
                this.parentNode.appendChild(childNode.cloneNode(true) );
            }
        }
    }
    
    public XNodeContext getContext() {
    	return context;
    }
    
    public XNode[] getPages() {
        return (XNode[])pageList.toArray(new XNode[pageList.size()]);
    }

    private int getPageCount() {
        return pageList.size();
    }
  
    private XNode getPage(int pageIndex) {
        return (XNode)pageList.get(pageIndex);
    }

    private static boolean isParagraphNode(XNode node) {
        return NodeNames.PAGE_NODE.equals(node.getNodeName() ) || 
            NodeNames.RECORD_NODE.equals(node.getNodeName() );
    }
    
    private void fireBeginEvent() {
        if (listener != null) {
            listener.begin(this);
        }
    }

    private void fireNewPageEvent() {
        if (listener != null) {
            listener.newPage(this, getPageCount() );
        }
    }

    private void fireEndEvent() {
        if (listener != null) {
            listener.end(this);
        }
    }
}
