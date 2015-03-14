package com.d_project.xprint.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.d_project.xprint.io.IResourceResolver;

public class XNodeContext {

	private IResourceResolver resolver;
	private Map imageCache;
	
    public XNodeContext(IResourceResolver resolver) {
    	this.resolver = resolver;
    	imageCache = new HashMap();
	}
    
    public InputStream getResourceAsStream(String path) throws IOException {
    	return resolver.getResourceAsStream(path);
    }

    public IDrawableImage getImage(String path) {
    	try {
			if (imageCache.containsKey(path) ) {
	        	return (IDrawableImage)imageCache.get(path);
	    	}
			IDrawableImage image = path.matches("(?i)^.+\\.svg$")?
					loadSVGImage(path) : loadBitmapImage(path);
			if (image != null) {
	            imageCache.put(path, image);
	    	}
	        return image;
    	} catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private IDrawableImage loadBitmapImage(String path) throws Exception {

    	InputStream in = getResourceAsStream(path);
    	try {
	    	BufferedImage image = ImageIO.read(in);
	    	return (image != null)? new BitmapImage(image) : null;
    	} finally {
    		in.close();
    	}
    }

    private IDrawableImage loadSVGImage(String path) throws Exception {
    	InputStream in = getResourceAsStream(path);
    	try {
	    	return GraphicsNodeImage.read(in);
    	} finally {
    		in.close();
    	}
    }
}