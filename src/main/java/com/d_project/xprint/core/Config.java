package com.d_project.xprint.core;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import xprint.ResourceManager;

/**
 * Config
 * @author Kazuhiko Arase
 */
public final class Config {

	private static final String CONFIG_FILE = "xprint-config.xml";
	
	private static final Config instance = new Config();
	
	public static Config getInstance() {
		return instance;
	}
	
	private final Logger logger = Logger.getLogger(getClass().getName() );
	
	private Document config;
	
	private Config() {

		config = loadConfig();
		
		if (config == null) {
        	logger.finer(CONFIG_FILE + " not found. use default.");
			config = getDefaultConfig();
		}
	}
	
	public boolean scriptEnabled() {
		return booleanProperty("script-enabled");
	}

	public boolean allowCrossDomain() {
		return booleanProperty("allow-cross-domain");
	}
	
	private boolean booleanProperty(String name) {
		NodeList nodes = config.getElementsByTagName(name);
		if (nodes.getLength() == 1) {
			return "true".equals(
				( (Element)nodes.item(0) ).getTextContent() );
		}
		return false;
	}
	
	public NodeList getFontPathList() {
		return config.getElementsByTagName("font-path");
	}
	
	public NodeList getFontAliasList() {
		return config.getElementsByTagName("font-alias");
	}
	
	private Document getDefaultConfig() {
        try {
        	Document config = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        	config.appendChild(config.createElement("config") );
        	return config;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
	}
	
    private Document loadConfig() {

        Document config = null;

        InputStream in = ResourceManager.getResourceManager().
            getResourceAsStream("/" + CONFIG_FILE);

        if (in != null) {

            try {

            	try {

	            	logger.finer("loading config");
	            	
	            	config = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
	                
	            	logger.finer("done.");
	                
                } finally {
	                in.close();
	            }
            
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        return config;
    }
}