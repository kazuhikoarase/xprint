package com.d_project.xprint.core;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import xprint.ResourceManager;

/**
 * FontManager
 * @author Kazuhiko Arase
 */
public final class FontManager {

	private static final FontManager instance = new FontManager();
	
	public static FontManager getInstance() {
		return instance;
	}
	
	private final Logger logger = Logger.getLogger(getClass().getName() ); 
	
    private Map fontMap;
    private Map fontAliasMap;
    private Map fontFileMap;
    private Map fontPathMap;
    
    private FontManager() {

        fontMap = new HashMap();
        fontAliasMap = new HashMap();
        fontFileMap = new HashMap();
        fontPathMap = new HashMap();

    	logger.finer("loading font alias");
    	loadFontSettings();
        logger.finer("done.");
    }
    
    private void loadFontSettings() {

        NodeList fontPathList = Config.getInstance().getFontPathList();

        for (int i = 0; i < fontPathList.getLength(); i++) {

            Element fontPath = (Element)fontPathList.item(i);

        	String family = fontPath.getAttribute("family");
        	String path = fontPath.getAttribute("path");

            logger.finer("font-path:" + family + "=" + path);
            fontPathMap.put(family, path);
        }

        NodeList fontAliasList = Config.getInstance().getFontAliasList();

        for (int i = 0; i < fontAliasList.getLength(); i++) {

            Element fontAlias = (Element)fontAliasList.item(i);

        	String family = fontAlias.getAttribute("family");
        	String alias = fontAlias.getAttribute("alias");

            logger.finer("font-alias:" + family + "=" + alias);
            fontAliasMap.put(family, alias);
        }
    }
    
    private String getFontPath(String family) {
        return (String)fontPathMap.get(family);
    }

    private Font loadFont(String path) {

        Font font = (Font)fontFileMap.get(path);

        if (font == null) {

            try {

                InputStream in = ResourceManager.getResourceManager().
                    getResourceAsStream(path);

            	if (in == null) {
	                in = new BufferedInputStream(new FileInputStream(path) );
            	}

                try {
                    font = Font.createFont(Font.TRUETYPE_FONT, in);
                    fontFileMap.put(path, font);
                } finally {
                    in.close();
                }
    
            } catch(Exception e) {
            	logger.severe(e.getMessage() );
            }
        }
        
        return font;

    }
    
    public synchronized Font getFont(String family) {

    	String alias = (String)fontAliasMap.get(family);
    	if (alias != null) {
    		family = alias;
    	}
    	
        Font font = (Font)fontMap.get(family);

        if (font == null) {
            
            String path = getFontPath(family);

            if (path != null) {
                font = loadFont(path);
            }

            if (font == null) {
                font = new Font(family, Font.PLAIN, 1);
            }
            
            fontMap.put(family, font);
        }

        return font;
        
    }
   
    public Font getFont(String family, float weight, float size) {

        Font font = getFont(family);

        Map attr = new HashMap();
        attr.put(TextAttribute.WEIGHT, new Float(weight) );
        attr.put(TextAttribute.SIZE, new Float(size) );

        return font.deriveFont(attr);        
    }
}
