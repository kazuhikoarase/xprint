package com.d_project.xprint.core;

import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import com.d_project.qrcode.ErrorCorrectionLevel;
import com.d_project.qrcode.QRCode;

/**
 * XQRCode
 * @author Kazuhiko Arase
 */
public class XQRCode extends XNode {
    
    private String text = null;
	private QRCode qrCode = null;
    private BufferedImage qrImage = null;
    
    public XQRCode() {
        super(NodeNames.QRCODE_NODE);
    }

    public Dimension2D getContentSize(XNodeContext context) {
    
    	prepareQRCode();
        
    	double unitWidth = getNumberAttribute(AttributeKeys.UNIT_WIDTH);
        int size = qrCode.getModuleCount();
        return new Size2D(size * unitWidth, size * unitWidth);
    }
    
    private int getErrorCorrectionLevel() {

    	String ecl = getAttribute(AttributeKeys.ERROR_CORRECTION_LEVEL);

    	if ("L".equals(ecl) ) {
    		return ErrorCorrectionLevel.L;
    	} else if ("Q".equals(ecl) ) {
    		return ErrorCorrectionLevel.Q;
    	} else if ("M".equals(ecl) ) {
	    	return ErrorCorrectionLevel.M;
    	} else {
    		return ErrorCorrectionLevel.H;
    	}
    }
    
    public void paint(XNodeContext context, IXGraphics g) {

    	prepareQRCode();

        double unitWidth = getNumberAttribute(AttributeKeys.UNIT_WIDTH);

        double cw = unitWidth * qrCode.getModuleCount();
        double ch = unitWidth * qrCode.getModuleCount();

		double x = getContentX(cw);
		double y = getContentY(ch);

		g.drawImage(qrImage, x, y, x + cw, y + cw);
    } 
    
	private void prepareQRCode() {

		String text = getText();

		if (qrCode == null || !text.equals(this.text) ) {

			String typeNumber = getAttribute(AttributeKeys.TYPE_NUMBER);
			QRCode qrCode = createQRCode(typeNumber, text);
			BufferedImage qrImage = new BufferedImage(
					qrCode.getModuleCount(),
					qrCode.getModuleCount(),
					BufferedImage.TYPE_INT_RGB);
			for (int row = 0; row < qrCode.getModuleCount(); row++) {
				for (int col = 0; col < qrCode.getModuleCount(); col++) {
					qrImage.setRGB(col, row, qrCode.isDark(row, col)?
							0x000000 : 0xffffff);
				}
			}

			this.qrCode = qrCode;
			this.qrImage = qrImage;
			this.text = text;
		}
	}

	private QRCode createQRCode(String typeNumber, String text) {
		
		if (typeNumber == null) {

			return QRCode.getMinimumQRCode(text, getErrorCorrectionLevel() );

		} else {

			try {
				QRCode qrCode = new QRCode();
				qrCode.setTypeNumber(Integer.parseInt(typeNumber) );
				qrCode.setErrorCorrectionLevel(getErrorCorrectionLevel() );
				qrCode.addData(text);
				qrCode.make();
				return qrCode;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
