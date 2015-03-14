package com.d_project.xprint.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.d_project.pdf.PDFDocument;
import com.d_project.xprint.io.IXWriterListener;
import com.d_project.xprint.io.XPDFWriter;

/**
 * PDFOutputDialog
 * @author Kazuhiko Arase
 */
public class PDFOutputDialog extends JDialog {

	private JProgressBar progressBar;

	private Thread thread;
	private XPDFWriter writer;
	private File file;

	public PDFOutputDialog(JFrame frame) {

		super(frame, true);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
				        	
	        }
	
	        public void windowOpened(WindowEvent e) {
	       		if (thread == null) {
	       			thread = new Thread(new CreateJob() );
	       			thread.start();
	       		}
	        } 
        } );

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout() );

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		p.add(BorderLayout.CENTER, progressBar);
		p.add(BorderLayout.NORTH, new JLabel("Creating PDF file...", JLabel.CENTER) );
		getContentPane().add(p);
		Action cancelAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		};
		cancelAction.putValue(Action.NAME, "Cancel");
		JButton cancelButton = new JButton(cancelAction);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(cancelButton);
		p.add(BorderLayout.SOUTH, buttonPanel);
	}

	public void start(XPDFWriter writer, File file) {

		this.writer = writer;
		this.file = file;

		progressBar.setMinimum(0);
		progressBar.setMaximum(writer.getPages().length);
		progressBar.setString("");
		pack();
		
		Point p1 = getParent().getLocationOnScreen();
		Dimension s1 = getParent().getSize();
		Dimension s2 = getSize();

		setLocation(
			p1.x + (s1.width - s2.width) / 2,
			p1.y + (s1.height - s2.height) / 2);

		setVisible(true);
	}

	private void cancel() {
		writer.setCanceled(true);
	}
	
	private class CreateJob implements Runnable, IXWriterListener {

		public void run() {

			try {

	 			PDFDocument document = writer.create(this);

                if (document != null) {

	                BufferedOutputStream out = new BufferedOutputStream(
	                	new FileOutputStream(file) );
	                try {
	                	document.save(out);
	                } finally {
		                out.close();
	                }

	                JOptionPane.showMessageDialog(PDFOutputDialog.this,
	                	Message.getMessage("msg.output_done", new Object[]{file.getName()} ),
	                	"", JOptionPane.INFORMATION_MESSAGE);
                }

			} catch(Exception e) {

		        JOptionPane.showMessageDialog(PDFOutputDialog.this, 
		            e.getMessage(), "", JOptionPane.ERROR_MESSAGE);

			} finally {
				dispose();
			}
		}

        public void onPage(int page) {
			progressBar.setValue(page + 1);
			progressBar.setString("Page " + (page + 1) + " of " + writer.getPages().length); 
        }
	}
}
