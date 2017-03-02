package com.d_project.xprint.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import com.d_project.xprint.core.AttributeKeys;
import com.d_project.xprint.core.XNode;
import com.d_project.xprint.core.XNodeContext;
import com.d_project.xprint.core.XPrintable;
import com.d_project.xprint.io.DefaultResourceResolver;
import com.d_project.xprint.io.XPDFWriter;
import com.d_project.xprint.parser.IPageParserListener;
import com.d_project.xprint.parser.PageParser;
import com.d_project.xprint.parser.XNodeLoader;

/**
 * MainFrame
 * @author Kazuhiko Arase
 */
public class MainFrame extends JFrame {
	
	private static final MainFrame instance = new MainFrame();
	
	public static MainFrame getInstance() {
		return instance;
	}

	private static final int FIT_WIDTH = -2;

	private static final int FIT_IN_WINDOW = -1;

    private final Object reloadLock = new Object();
	
	private XNodeContext context;
    private XNode[] pages;
    private String title;

    private FileChooseField formField;
    private FileChooseField dataField;
    private PageViewer viewer;
    private JScrollPane viewerScrollPane;
    private JLabel statusBar;
    private JComboBox scaleCombo;
	private JCheckBox showBoundingBoxCheckBox;
	private JCheckBoxMenuItem showBoundingBoxCheckBoxMenuItem;
	
    private Thread thread;

    private MainFrame() {

        super("PageViewer");

        setIconImages(Arrays.asList(
            new ImageIcon(getClass().
                getResource("xprint_logo_x32.png") ).getImage(),
            new ImageIcon(getClass().
                getResource("xprint_logo_x128.png") ).getImage()
        ) );

        //-----------------------------
        // prev

        Action prevAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                prevPage();
            }
        };
        prevAction.putValue(Action.NAME, "Prev");
        prevAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK) );


        //-----------------------------
        // next

        Action nextAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                nextPage();
            }
        };
        nextAction.putValue(Action.NAME, "Next");
        nextAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK) );

        //-----------------------------
        // reload

        Action reloadAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        };
        reloadAction.putValue(Action.NAME, "Reload");
        reloadAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0) );
        reloadAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R) );

        //-----------------------------
        // print

        Action printAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                print();
            }
        };
        printAction.putValue(Action.NAME, "Print");
        printAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK) );
        printAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P) );

        //-----------------------------
        // pdf

        Action pdfAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                pdf();
            }
        };
        pdfAction.putValue(Action.NAME, "PDF");
        pdfAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK) );
        pdfAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D) );

        //-----------------------------
        // exit

        Action exitAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        };
        exitAction.putValue(Action.NAME, "Exit");
        exitAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_X) );

        //-----------------------------
        // showBoundingBox

        Action showBoundingBoxAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                updateBoundingBox(e.getSource() );
            }
        };
        
        showBoundingBoxAction.putValue(Action.NAME, "Show Bounding Box");
        showBoundingBoxAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_H) );


        //-----------------------------
        // scale

        ScaleItem[] scaleItems = new ScaleItem[] {
            new ScaleItem("Fit Width", FIT_WIDTH),
            new ScaleItem("Fit In Window", FIT_IN_WINDOW),
            new ScaleItem("800%", 800),
            new ScaleItem("400%", 400),
            new ScaleItem("200%", 200),
            new ScaleItem("150%", 150),
            new ScaleItem("125%", 125),
            new ScaleItem("100%", 100),
            new ScaleItem("50%", 50),
            new ScaleItem("25%", 25),
            new ScaleItem("10%", 10)
        };

        scaleCombo = new JComboBox(scaleItems) {

            public Dimension getPreferredSize() {
                return super.getPreferredSize();
            }

            public Dimension getMaximumSize() {
                Dimension maxiSize = super.getMaximumSize();
                Dimension prefSize = super.getPreferredSize();
                return new Dimension(prefSize.width, maxiSize.height);
            }
        };

        scaleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        scaleCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        scaleCombo.setSelectedIndex(0);
        scaleCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateViewer();
                }
            }
        });

        
		showBoundingBoxCheckBox = new JCheckBox(showBoundingBoxAction);
        
        JToolBar bar = new JToolBar();
        bar.add(prevAction);
        bar.add(nextAction);
        bar.add(reloadAction);      
        bar.add(printAction);      
        bar.add(pdfAction);      
        bar.add(scaleCombo);        
        bar.add(showBoundingBoxCheckBox);        
		
		showBoundingBoxCheckBoxMenuItem = new JCheckBoxMenuItem(showBoundingBoxAction);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(printAction);
        fileMenu.add(pdfAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(prevAction);
        viewMenu.add(nextAction);
        viewMenu.addSeparator();
        viewMenu.add(showBoundingBoxCheckBoxMenuItem);
        viewMenu.addSeparator();
        viewMenu.add(reloadAction);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
                
        setJMenuBar(menuBar);

        //-----------------------------

        formField = new FileChooseField("Form File: ");
        dataField = new FileChooseField("Data File: ");
        
        Box formAndDataBox  = Box.createVerticalBox();
        formAndDataBox.add(formField);
        formAndDataBox.add(dataField);

        viewer = new PageViewer();
        viewerScrollPane = new JScrollPane(viewer);
        viewerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        viewerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout() );
        mainPane.add(BorderLayout.NORTH, formAndDataBox);
        mainPane.add(BorderLayout.CENTER, viewerScrollPane);
        statusBar = new JLabel();
        
//        getContentPane().add(BorderLayout.PAGE_START, bar);
        getContentPane().add(BorderLayout.BEFORE_FIRST_LINE, bar);

        getContentPane().add(BorderLayout.CENTER, mainPane);

//        getContentPane().add(BorderLayout.PAGE_END, statusBar);
        getContentPane().add(BorderLayout.AFTER_LAST_LINE, statusBar);

        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });  
        
        viewerScrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateViewer();
            }
        });
                    
    }
	
    private void updateBoundingBox(Object source) {
    	if (source == showBoundingBoxCheckBox) {
			showBoundingBoxCheckBoxMenuItem.setSelected(showBoundingBoxCheckBox.isSelected() );
			viewer.setShowBoundingBox(showBoundingBoxCheckBox.isSelected() );
    	} else {
			showBoundingBoxCheckBox.setSelected(showBoundingBoxCheckBoxMenuItem.isSelected() );
			viewer.setShowBoundingBox(showBoundingBoxCheckBoxMenuItem.isSelected() );
    	}
    }
    
    private void updateViewer() {

        ScaleItem item = (ScaleItem)scaleCombo.getSelectedItem();

        if (item.getScale() > 0) {

        	int dpi = Toolkit.getDefaultToolkit().
	    		getScreenResolution();

            viewer.setScale(item.getScale() / 100.0 * dpi / 72.0);

        } else if (item.getScale() == FIT_IN_WINDOW) {

            // 自動サイズ調整
            XNode page = viewer.getCurrentPage();

            double scale = 1.0;
            
            if (page != null) {

				Rectangle viewRect =  viewerScrollPane.getViewportBorderBounds();
                
                double width = page.getNumberAttribute(AttributeKeys.WIDTH);
                double height = page.getNumberAttribute(AttributeKeys.HEIGHT);

                if ( (double)viewRect.width / viewRect.height > width / height) {
                    // たて
                    scale = Math.max(0, (viewRect.height - viewer.getPadding() * 2) / height);                    
                } else {
                    // よこ
                    scale = Math.max(0, (viewRect.width - viewer.getPadding() * 2) / width);                    
                }
            }

            if (scale > 0) {
                viewer.setScale(scale);
            }
        	
        } else if (item.getScale() == FIT_WIDTH) {

            // 自動サイズ調整
            XNode page = viewer.getCurrentPage();

            double scale = 1.0;
            
            if (page != null) {
                
                double width = page.getNumberAttribute(AttributeKeys.WIDTH);
				Rectangle viewRect =  viewerScrollPane.getViewportBorderBounds();
				scale = Math.max(0, (viewRect.width - viewer.getPadding() * 2) / width);                    
            }

            if (scale > 0) {
                viewer.setScale(scale);
            }

        }

        viewer.invalidate();
        validate();
    }
    
    private void print() {

        try {

            XNode[] pages = getPages();
            
            if (pages != null) {

                PrinterJob job = PrinterJob.getPrinterJob();

                if (!job.printDialog() ) {
                    return;
                }
                
                XPrintable printable = new XPrintable();
                printable.setPages(context, pages);

                job.setPrintable(
            		printable,
            		printable.getDefaultPageFormat() );
                job.print();
            } 

        } catch(Throwable t) {
        	handleThrowable(t);
        }
    }

    private void pdf() {

        XNode[] pages = getPages();
        
        if (pages == null) {
        	return;
        }

        Config config = Config.getInstance();
	
	    String currentDirectory = config.getCurrentDirectory();
	    if (currentDirectory == null) {
	        currentDirectory = ".";
	    }
	    
	    JFileChooser fc = new JFileChooser();
	    fc.setFileFilter(new PDFFilter() );
	    fc.setCurrentDirectory(new File(currentDirectory) );

	    String fileName = "output.pdf";

	    if (title != null) {
	    	int index = title.lastIndexOf('.');
	    	if (index != -1) {
	    		fileName = title.substring(0, index) + ".pdf"; 
	    	} else {
	    		fileName = title + ".pdf";
	    	}
	    }
	    
		fc.setSelectedFile(new File(currentDirectory, fileName) );
					    
	    if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
	        return;
	    }

        File file = fc.getSelectedFile();
        if (file == null) {
        	return;
        }

		if (file.exists() ) {
			if (JOptionPane.showConfirmDialog(this,
					Message.getMessage("msg.confirm_overwrite", new Object[]{fileName}),
            		"", JOptionPane.YES_NO_OPTION,
            		JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)  {
    			return;
            }
		}
		                
        XPDFWriter writer = new XPDFWriter();
        
        writer.setPages(context, pages);
        
		PDFOutputDialog dialog = new PDFOutputDialog(this);
		dialog.start(writer, file);
    }

    private void reload() {

        synchronized(reloadLock) {

            if (thread != null) {
                return;
            }

            thread = new Thread(new LoadJob() );
            thread.start();
        }

    }
    
    private void exit() {

        Config config = Config.getInstance();

        config.clearFormFiles();        
        for (int i = 0; i < formField.getHistoryCount(); i++) {
            config.addFormFile(formField.getHistory(i) );
        }
        
        config.clearDataFiles();        
        for (int i = 0; i < dataField.getHistoryCount(); i++) {
            config.addDataFile(dataField.getHistory(i) );
        }
            
        config.save();
        
        System.exit(0);
    }
    
    public void start() {

        Config config = Config.getInstance();

        config.load();
        
        for (int i = 0; i < config.getFormFileCount(); i++) {
            formField.addHistory(config.getFormFile(i) );
        }
        
        for (int i = 0; i < config.getDataFileCount(); i++) {
            dataField.addHistory(config.getDataFile(i) );
        }

        setSize(640, 480);
		setVisible(true);
    }
        
    private void updateStatus() {

        if (viewer.getPageCount() > 0) {
            statusBar.setText("Page " + (viewer.getPageIndex() + 1) + " of " + viewer.getPageCount() );
        } else {
            statusBar.setText("");
        }
    }
    
    private void prevPage() {
        viewer.setPageIndex(Math.max(0, viewer.getPageIndex() - 1) );
        updateStatus();
    }
    
    private void nextPage() {
        viewer.setPageIndex(Math.min(viewer.getPageCount() - 1, viewer.getPageIndex() + 1) );
        updateStatus();
    }

    public void setPages(XNodeContext context, XNode[] pages, String title) {
        this.context = context;
        this.pages = pages;
		this.title = title;
    }
    
    public XNode[] getPages() {
        return pages;
    }
    
    public void handleThrowable(Throwable t) {
    	
        t.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            t.getMessage(), "", JOptionPane.ERROR_MESSAGE);
    }

    private class Updater implements IPageParserListener {

        public void begin(PageParser pager) {
        }

        public void end(PageParser pager) {
        }

        public void newPage(PageParser pager, int pageIndex) {
            statusBar.setText("Parsing " + (pageIndex + 1) + " pages...");
        }
    }
    
    private class LoadJob implements Runnable {

    	public void run() {
  
            try {

                statusBar.setText("Loading files...");

                URL form = toURL(formField.getCurrentPath() );
                URL data = null;
                
                if (dataField.getCurrentPath().length() > 0) {
                    data = toURL(dataField.getCurrentPath() );
                }
                
                XNode page = XNodeLoader.load(form, data);

                XNodeContext context = new XNodeContext(
                		new DefaultResourceResolver(form) );
                
                statusBar.setText("Done.");
                
                PageParser pager = new PageParser(context, (XNode)page);
                pager.setPageParserListener(new Updater() );
                pager.createPages();
                
                String title = (data != null)?
                	data.getFile() : form.getFile();
                
                setPages(
            		pager.getContext(),
            		pager.getPages(),
            		title
            	);
	            
                viewer.setPages(
	            	pager.getContext(),
	            	pager.getPages() );

	            updateViewer();
    
            } catch(Throwable t) {
            	handleThrowable(t);
            }

            updateStatus();

            synchronized(reloadLock) {
                thread = null;
            }
        }
    	
    	private URL toURL(String path) throws MalformedURLException {
    		return (path.indexOf("//") != -1)?
				new URL(path) : new File(path).toURL();
    	}
    }
    
    private static class ScaleItem {
        
        private String label;
        private int scale;
        
        public ScaleItem(String label, int scale) {
            this.label = label;
            this.scale = scale;
        }
        
        public String toString() {
            return label;
        }
        
        public int getScale() {
            return scale;
        }
    }
    
    private static class PDFFilter extends FileFilter {
        
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".pdf");
        }
    
        public String getDescription() {
            return "PDF Files";
        }
    }              
}
