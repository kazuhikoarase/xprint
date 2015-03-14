package com.d_project.xprint.ui;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

/**
 * Message
 * @author Kazuhiko Arase
 */
class Message {
	
	private static final Properties props;
	
	static {
		
		props = new Properties();

		try {
			InputStream in = getResource();
			try {
				props.loadFromXML(in);
			} finally {
				in.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String getMessage(String key, Object[] args) {
		return MessageFormat.format(props.getProperty(key), args);
	}
	
	private static InputStream getResource() {
		InputStream in = Message.class.getResourceAsStream("Message_" +
			Locale.getDefault() + ".xml");
		if (in == null) {
			// use default
			in = Message.class.getResourceAsStream("Message.xml");
		}
		return in;
	}

	private Message() {
	}
}