package com.d_project.pdf.writer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.d_project.pdf.PDFArray;
import com.d_project.pdf.PDFCatalog;
import com.d_project.pdf.PDFDeflateStream;
import com.d_project.pdf.PDFDictionary;
import com.d_project.pdf.PDFDocument;
import com.d_project.pdf.PDFImage;
import com.d_project.pdf.PDFName;
import com.d_project.pdf.PDFOutputStream;
import com.d_project.pdf.PDFPage;
import com.d_project.pdf.PDFPageTreeNode;
import com.d_project.pdf.PDFRectangle;
import com.d_project.pdf.PDFStream;
import com.d_project.pdf.Util;

/**
 * PDFWriter
 * @author Kazuhiko Arase
 */
public class PDFWriter {

    private double pageWidth;
    private double pageHeight;

    private ByteArrayOutputStream bout;
    private PDFOutputStream pout;

	private Font font;
    private Stroke stroke;

    private double offsetX;
    private double offsetY;

    private Color color;

    private List contents;

    private List images;

    public PDFWriter() throws IOException {

        this.contents = new ArrayList();
        this.images = new ArrayList();

        this.pageWidth = 595;
        this.pageHeight = 842;

        this.offsetX = 0;
        this.offsetY = 0;

		this.font = null;
		
        this.stroke = new BasicStroke(0.75f);
    }

    public void beginPage() throws IOException {

        bout = new ByteArrayOutputStream(1024);
//        DeflaterOutputStream dout = new DeflaterOutputStream(bout);
        pout = new PDFOutputStream(bout);
        pout.print(" 1 0 0 -1");
        pout.print(" " + format(0) );
        pout.print(" " + format(pageHeight) );
        pout.print(" cm");
    }

    public void endPage() throws IOException {
        pout.close();
        PDFStream content = new PDFDeflateStream(bout.toByteArray() );
        contents.add(content);
    }

    public void setPageSize(int pageWidth, int pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }
	
    public void translate(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
    }
    
    public void setStroke(Stroke stroke) {
    	this.stroke = stroke;
    }
    
    public Stroke getStroke() {
    	return stroke;
    }
    
    public void setFont(Font font) {
    	this.font = font;
    }

	public Font getFont() {
		return font;
	}

    public void setColor(Color color) {
        this.color = color;
    }

    public void drawLine(double x1, double y1, double x2, double y2) throws IOException {
		draw(new Line2D.Double(x1, y1, x2, y2) );
    }

    public void drawRect(double x, double y, double width, double height) throws IOException {
 		draw(new Rectangle2D.Double(x, y, width, height) );
    }

    public void fillRect(double x, double y, double width, double height) throws IOException {
 		fill(new Rectangle2D.Double(x, y, width, height) );
    }
    
    public void draw(Shape shape) throws IOException {
    	fill(stroke.createStrokedShape(shape) );
    }

    public void fill(Shape shape) throws IOException {

        pout.print(" q");

        pout.print(" 1 0 0 1");
        pout.print(" " + format(offsetX) );
        pout.print(" " + format(offsetY) );
        pout.print(" cm");

        pout.print(getRGB() );
        pout.print(" rg");

		path(shape);

        pout.print(" f Q");
		
    }

/*
    public void drawString2(String s, double x, double y) throws IOException {

        x += offsetX;
        y += offsetY;

        // begin text
		pout.print(" BT");

        // フォントとサイズ

        switch (fontStyle) {
        case FONT_STYLE_SANS_SERIF : 
            pout.print(" /F1 " + format(fontSize) + " Tf");
            break;
        case FONT_STYLE_SERIF : 
            pout.print(" /F2 " + format(fontSize) + " Tf");
            break;
        default :
            pout.print(" /F1 " + format(fontSize) + " Tf");
            break;
        }

        pout.print(" /F1 " + format(getFontSize() ) + " Tf");

        // レンダリングモード(塗り)
        pout.print(" 0 Tr");

        // 塗りの色
        pout.print(getRGB() );
        pout.print(" rg");

        // 位置
        pout.print(" 1 0 0 1 " + format(x) + " " + format(y) + " Tm");

        // 文字列
        pout.print(" ");
        pout.writeObject(new PDFLiterealString(getBytes(s, "MS932") ) );
        pout.print(" Tj");

        // end text
        pout.print(" ET");

    }
*/

    public void drawString(String s, double x, double y) 
    throws IOException {
    	
		FontRenderContext frc = new FontRenderContext(null, true, true);

		TextLayout layout = new TextLayout(s, getFont(), frc);

		Shape shape = layout.getOutline(new AffineTransform(1, 0, 0, 1, x, y) );

    	fill(shape);
    }

    public void drawImage(PDFImage image, double x, double y, double width, double height) throws IOException {

        x += offsetX;
        y += offsetY;

        images.add(image);

        if (width > 0 && height == 0) {
            //  高さを幅に合わせて再計算
            height = width * image.getHeight() / image.getWidth();
        } else if (width == 0 && height > 0) {
            //  幅を高さに合わせて再計算
            width = height * image.getWidth() / image.getHeight();
        }

        pout.print(" q");

        // 位置
        pout.print(" 1 0 0 -1 " + format(x) + " " + format(y + height) + " cm");

        // サイズ
        pout.print(" " + format(width) + " 0 0 " + format(height) + " 0 0 cm");

        pout.print(" /Im" + (images.size() - 1) + " Do");
        pout.print(" Q");

    }

    public PDFDocument getDocument() throws IOException {

        PDFDocument doc = new PDFDocument();

        PDFArray procSet = new PDFArray();
        procSet.add(new PDFName("PDF") );
/*
        procSet.add(new PDFName("Text") );
*/        
        doc.addObject(procSet);

/*
        PDFDictionary font = new PDFDictionary();
        font.put("F1", PDFFont.createJapaneseFont(doc, false) );
        font.put("F2", PDFFont.createJapaneseFont(doc, true) );
        doc.addObject(font);
*/
        PDFDictionary xObject = new PDFDictionary();

        
        for (int i = 0; i < images.size(); i++) {
            PDFStream image = (PDFStream)images.get(i);
            String name = "Im" + i;
            doc.addObject(image);
            xObject.put(name, image);
        }
            
        PDFDictionary resources = new PDFDictionary();
        resources.put("ProcSet", procSet);
/*        
        resources.put("Font", font);
*/
        resources.put("XObject", xObject);
        doc.addObject(resources);

        PDFRectangle mediaBox = new PDFRectangle(0, 0, (int)pageWidth, (int)pageHeight);
//        PDFRectangle mediaBox = new PDFRectangle(0, pageHeight, pageWidth, 0);
        doc.addObject(mediaBox);

        PDFPageTreeNode pages = new PDFPageTreeNode();
        pages.put("Resources", resources);
        pages.put("MediaBox", mediaBox);
        doc.addObject(pages);

        PDFCatalog catalog = new PDFCatalog();
        catalog.setPages(pages);
        doc.addObject(catalog);

        for (int i = 0; i < contents.size(); i++) {

            PDFStream content = (PDFStream)contents.get(i);
            doc.addObject(content);

            PDFPage page = new PDFPage();
            page.setContents(content);

            pages.addPage(page);
            doc.addObject(page);
        }

        doc.getTrailer().setRoot(catalog);

        return doc;
    }

    private static String format(double b) {
    	String s = Util.format( (long)(b * 10000), 5);
		String iPart = s.substring(0, s.length() - 4);
		String fPart = s.substring(s.length() - 4);
		return iPart + "." + fPart;
    }

    private String getRGB() {

    	StringBuffer buffer = new StringBuffer();

    	buffer.append(' ');
        buffer.append(format(color.getRed()   / 255.0) );
    	buffer.append(' ');
        buffer.append(format(color.getGreen() / 255.0) );
    	buffer.append(' ');
        buffer.append(format(color.getBlue()  / 255.0) );

        return buffer.toString();
    }

    private void path(Shape shape) throws IOException {

		PathIterator it = shape.getPathIterator(null);

		double[] coord = new double[6];

		switch (it.getWindingRule() ) {

		case PathIterator.WIND_NON_ZERO :
			pout.print(" W");
			break;

		case PathIterator.WIND_EVEN_ODD :
			pout.print(" W*");
			break;
		default :
			break;
		}
		
		double[] lastPoint = null;
		
		while (!it.isDone() ) {
			
			int seg = it.currentSegment(coord);
			
			switch (seg) {

			case PathIterator.SEG_MOVETO :
		        for (int i = 0; i < 2; i++) {
			        pout.print(" " + format(coord[i]) );
		        }
				pout.print(" m");
				lastPoint = new double[]{coord[0], coord[1]};
				break;

			case PathIterator.SEG_LINETO :
		        for (int i = 0; i < 2; i++) {
			        pout.print(" " + format(coord[i]) );
		        }
				pout.print(" l");
				lastPoint = new double[]{coord[0], coord[1]};
				break;

			case PathIterator.SEG_CUBICTO :
		        for (int i = 0; i < 6; i++) {
			        pout.print(" " + format(coord[i]) );
		        }
				pout.print(" c");
				lastPoint = new double[]{coord[4], coord[5]};
				break;

			case PathIterator.SEG_QUADTO :
		        pout.print(" " + format(lastPoint[0] / 3 + coord[0] * 2 / 3) );
		        pout.print(" " + format(lastPoint[1] / 3 + coord[1] * 2 / 3) );
		        pout.print(" " + format(coord[0] * 2 / 3 + coord[2] / 3) );
		        pout.print(" " + format(coord[1] * 2 / 3 + coord[3] / 3) );
		        pout.print(" " + format(coord[2]) );
		        pout.print(" " + format(coord[3]) );
				pout.print(" c");
				lastPoint = new double[]{coord[2], coord[3]};
				break;

			case PathIterator.SEG_CLOSE :
				pout.print(" h");
				lastPoint = null;
				break;
				
			default :
				throw new RuntimeException("seg:" + seg);
			}
			
			it.next();
		}
    }
}

