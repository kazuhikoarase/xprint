package xprint;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

import com.d_project.xprint.core.XNode;
import com.d_project.xprint.core.XNodeContext;
import com.d_project.xprint.core.XPrintable;
import com.d_project.xprint.io.DefaultResourceResolver;
import com.d_project.xprint.io.XPDFWriter;
import com.d_project.xprint.io.XRawWriter;
import com.d_project.xprint.parser.PageParser;
import com.d_project.xprint.parser.XNodeLoader;


/**
 * XPrint
 * @author Kazuhiko Arase
 */
public class XPrint {
    
	private XNodeContext context = null;

	private XNode[] pages = null;

    /**
     * コンストラクタ
     */
    public XPrint() {
    }

    /**
     * ページ数を取得する。
     * @return ページ数
     * @throws XPrintException
     */
    public int getPageCount() throws XPrintException {
    	if (pages == null) {
    		throw new XPrintException("no form and data is loaded.");
    	}
    	return pages.length;
    }
    
    /**
     * フォームとデータを読み込んで印刷の準備を行う。
     * @param formFile フォームファイル
     * @param dataNode データノード
     * @exception XPrintException 読込中にエラーが発生
     */
    public void load(File formFile, Node dataNode) 
    throws XPrintException {
    	try {
    		load(formFile.toURL(), dataNode);
    	} catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * フォームとデータを読み込んで印刷の準備を行う。
     * @param formFile フォームファイル
     * @param dataFile データファイル
     * @exception XPrintException 読込中にエラーが発生
     */
    public void load(File formFile, File dataFile)
    throws XPrintException {
        try {
            load(formFile.toURL(),
            	(dataFile != null)? dataFile.toURL() : null);
        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * フォームとデータを読み込んで印刷の準備を行う。
     * @param form フォーム
     * @param dataNode データノード
     * @exception XPrintException 読込中にエラーが発生
     */
    public void load(URL form, Node dataNode)
    throws XPrintException {
        try {
            loadImpl(form, XNodeLoader.load(form, 
        		(dataNode != null)? new DOMSource(dataNode) : null) );
        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * フォームとデータを読み込んで印刷の準備を行う。
     * @param form フォーム
     * @param data データ
     * @exception XPrintException 読込中にエラーが発生
     */
    public void load(URL form, URL data)
    throws XPrintException {
        try {
            loadImpl(form, XNodeLoader.load(form, data) );
        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * フォームとデータを読み込んで印刷の準備を行う。
     * @param baseUrl ベースURL（null可)
     * @param form フォーム
     * @param data データ
     * @exception XPrintException 読込中にエラーが発生
     */
    public void load(URL baseUrl, Source form, Source data)
    throws XPrintException {
        try {
            loadImpl(baseUrl, XNodeLoader.load(form, data) );
        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }
    
    private void loadImpl(URL baseUrl, XNode page)
    throws Exception {

        XNodeContext context = new XNodeContext(
    		new DefaultResourceResolver(baseUrl) );

        PageParser pager = new PageParser(context, page);
        pager.createPages();

        this.context = pager.getContext();
        this.pages = pager.getPages();
    }

    /**
     * 印刷を行う。
     * @param job 印刷するプリンター・ジョブ
     * @exception XPrintException 印刷中にエラーが発生
     */
    public void print(PrinterJob job) 
    throws XPrintException {
		
    	if (pages == null) {
    		throw new XPrintException("no form and data is loaded.");
    	}

		try {

            XPrintable printable = new XPrintable();
            printable.setPages(context, pages);

            job.setPrintable(
        		printable,
        		printable.getDefaultPageFormat() );
            job.print();

        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * PDF を出力する。
     * @param out 出力ストリーム
     * @exception XPrintException 出力中にエラーが発生
     */
    public void outputPDF(OutputStream out)
    throws XPrintException {

		if (pages == null) {
    		throw new XPrintException("no form and data is loaded.");
    	}
    	
    	try {

	        XPDFWriter writer = new XPDFWriter();
	        writer.setPages(context, pages);
	        writer.create(null).save(out);

        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * RAW データを出力する。
     * @param out 出力ストリーム
     * @exception XPrintException 出力中にエラーが発生
     */
    public void outputRaw(OutputStream out)
    throws XPrintException {

    	if (pages == null) {
    		throw new XPrintException("no form and data is loaded.");
    	}

    	try {

	        XRawWriter writer = new XRawWriter();
	        writer.setPages(context, pages);
	        writer.create(out, null);

        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * フォームファイルとデータノードを指定して印刷を実行する。
     * @param job 印刷するプリンター・ジョブ
     * @param formFile フォームファイル
     * @param dataNode データノード
     * @return ページ数
     * @exception XPrintException 印刷中にエラーが発生
     * @deprecated
     */
    public static int print(PrinterJob job, File formFile, Node dataNode)
    throws XPrintException {

        try {

    		XPrint xp = new XPrint();
    		xp.load(formFile, dataNode);
    		xp.print(job);

	        return xp.getPageCount();

        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }
    
    /**
     * フォームファイルとデータファイルを指定して印刷を実行する。
     * @param job 印刷するプリンター・ジョブ
     * @param formFile フォームファイル
     * @param dataFile データファイル
     * @return ページ数
     * @exception XPrintException 印刷中にエラーが発生
     * @deprecated
     */
    public static int print(PrinterJob job, File formFile, File dataFile)
    throws XPrintException {

        try {

    		XPrint xp = new XPrint();
    		xp.load(formFile, dataFile);
    		xp.print(job);

	        return xp.getPageCount();

        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }

    /**
     * フォームファイルとデータノードを指定してPDFを出力する。
     * @param out 出力ストリーム
     * @param formFile フォームファイル
     * @param dataNode データノード
     * @return ページ数
     * @exception XPrintException 印刷中にエラーが発生
     * @deprecated
     */
    public static int outputPDF(OutputStream out, File formFile, Node dataNode) throws XPrintException {

    	try {
    		
    		XPrint xp = new XPrint();
    		xp.load(formFile, dataNode);
    		xp.outputPDF(out);

	        return xp.getPageCount();
	        
        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }
    
    /**
     * フォームファイルとデータファイルを指定してPDFを出力する。
     * @param out 出力ストリーム
     * @param formFile フォームファイル
     * @param dataFile データファイル
     * @return ページ数
     * @exception XPrintException 印刷中にエラーが発生
     * @deprecated
     */
    public static int outputPDF(OutputStream out, File formFile, File dataFile) throws XPrintException {

    	try {
	
    		XPrint xp = new XPrint();
    		xp.load(formFile, dataFile);
    		xp.outputPDF(out);

	        return xp.getPageCount();

        } catch (Exception e) {
            throw new XPrintException(e);
        }
    }
}
