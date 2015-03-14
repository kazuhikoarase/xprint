package com.d_project.xprint.io;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

/**
 * ImageUtil
 * @author Kazuhiko Arase
 */
public class ImageUtil {

	private ImageUtil() {
	}

	public static byte[] getJpegData(
		BufferedImage image,
		double scale,
		float quality
	) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			writeJpegData(image, scale, quality, out);		 	
		} finally {
			out.close();
		}

		return out.toByteArray();
	}

	public static void writeJpegData(
		BufferedImage image,
		double scale,
		float quality,
		OutputStream out
	) throws IOException {

		if (scale != 1.0) {
			AffineTransformOp atop = new AffineTransformOp(
		    	new AffineTransform(scale, 0, 0, scale, 0, 0),
		        	AffineTransformOp.TYPE_BILINEAR);
		    image = atop.filter(image, null);
		}

		if (image.getType() != BufferedImage.TYPE_INT_RGB) {
	        ColorConvertOp ccop = new ColorConvertOp(null);
	        image = ccop.filter(image, new BufferedImage(
	        		image.getWidth(), 
	        		image.getHeight(),
	        		BufferedImage.TYPE_INT_RGB) );
		}

		ImageWriteParam param = new JPEGImageWriteParam(null);
		param.setProgressiveMode(JPEGImageWriteParam.MODE_DISABLED);
		param.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);

		ImageOutputStream imageOut = ImageIO.createImageOutputStream(out);
		
		ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("jpg").next();

		try {

			writer.setOutput(imageOut);

			// 画像書き込み
			writer.write(null, new IIOImage(image, null, null), param);

		} finally {
			writer.dispose();
		}
	}
}