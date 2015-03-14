
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import xprint.XPrint;

public class Sample1 {

	public static void main(String[] args) throws Exception {
		new Sample1().start();
	}

	private final Logger logger = Logger.getLogger(getClass().getName() );
	
	private File baseDir;

	private File formsDir;

	private File outputDir;
	
	public void start() throws Exception {

		baseDir = new File(".");
		formsDir = new File(baseDir, "forms");
		outputDir = new File(baseDir, "out");

		output("sample1", "sample1-form.xml", null);
		output("sample2", "sample2-form.xml", null);
		output("sample3", "sample3-form.xml", "sample3-data.xml");
		output("sample4", "sample4-form.xml", null);
		output("sample5", "sample5-form.xml", "sample5-data.xml");
		output("sample6", "sample6-form.xml", null);
		output("sample7", "sample7-form.xml", "sample7-data.xml");
		output("sample8", "sample8-form.xml", "sample8-data.xml");
		output("sample9", "sample9-form.xml", "sample9-data.xml");
		
	}

	private void output(String outputName, String formFileName, String dataFileName)
	throws Exception {
		
		{
			File outputFile = new File(outputDir, outputName + ".pdf");
			prepareDir(outputFile);
			outputPDF(outputFile, formFileName, dataFileName);
		}
		
		{
			File outputFile = new File(outputDir, outputName + ".dat");
			prepareDir(outputFile);
			outputRaw(outputFile, formFileName, dataFileName);
		}
	}

	private void prepareDir(File file) {
		File dir = file.getParentFile();
		if (!dir.exists() ) {
			logger.info("create dir " + dir.getAbsolutePath() );
			dir.mkdirs();
		}
	}

	private void outputPDF(File outputFile, String formFileName, String dataFileName)
	throws Exception {

		logger.info("output pdf " + outputFile.getAbsolutePath() );

		OutputStream out = new BufferedOutputStream(
				new FileOutputStream(outputFile) );
		
		try {
			XPrint xp = new XPrint();
			xp.load(new File(formsDir, formFileName), 
					(dataFileName != null)? new File(formsDir, dataFileName) : null);
			xp.outputPDF(out);
		} finally {
			out.close();
		}

		logger.info("done.");
	}

	private void outputRaw(File outputFile, String formFileName, String dataFileName)
	throws Exception {

		logger.info("output raw " + outputFile.getAbsolutePath() );
		
		OutputStream out = new BufferedOutputStream(
				new FileOutputStream(outputFile) );
		
		try {
			XPrint xp = new XPrint();
			xp.load(new File(formsDir, formFileName), 
					(dataFileName != null)? new File(formsDir, dataFileName) : null);
			xp.outputRaw(out);
		} finally {
			out.close();
		}

		logger.info("done.");
	}
}