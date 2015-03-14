package com.d_project.xprint.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 * FileChooseField
 * @author Kazuhiko Arase
 */
public class FileChooseField extends JPanel {

    private DefaultComboBoxModel model;
    private JComboBox combo;

    public FileChooseField(String label) {

    	setLayout(new BorderLayout(0, 0) );
    	
        add(new JLabel(label, JLabel.RIGHT) {
        	public Dimension getPreferredSize() {
        		Dimension size = super.getPreferredSize();
        		size.width = 70;
        		return size;
        	}
        }, BorderLayout.WEST);

        model = new DefaultComboBoxModel();
        combo = new JComboBox(model);
        combo.setEditable(true);

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSelectedFile( (String)combo.getSelectedItem() );
            }
        });
        
        add(combo, BorderLayout.CENTER);
        
        JButton refButton = new JButton();
        add(refButton, BorderLayout.EAST);
        
        Action action;
        
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectFile();                   
            }
        };
        action.putValue(Action.NAME, "...");
        refButton.setAction(action);
    }
    
    public String getCurrentPath() {
//      return (String)model.getSelectedItem();
    	String file = (String)combo.getEditor().getItem();
    	return (file != null)? file : "";
    }

    public void clearHistories() {
        model.removeAllElements();
    }

    public void addHistory(String file) {
        model.addElement(file);
    }
    
    public String getHistory(int index) {
        return (String)model.getElementAt(index);
    }

    public int getHistoryCount() {
        return model.getSize();
    }

    private void updateSelectedFile(String file) {

        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(file) ) {
                model.removeElementAt(i);
            }
        }

        model.insertElementAt(file, 0);
        combo.setSelectedItem(file);

        int maxFiles = 20;
        while (model.getSize() > maxFiles) {
            model.removeElementAt(model.getSize() - 1);
        }
    }
    
    private void selectFile() {
        
        Config config = Config.getInstance();

        String currentDirectory = null;
        
        if (getCurrentPath().length() > 0) {
        	File file = new File(getCurrentPath() );
        	if (file.exists() ) {
        		if (file.isFile() ) {
        			currentDirectory = file.getParent();
        		} else if (file.isDirectory() ) {
        			currentDirectory = file.getPath();
        		}
        	}
        }
 
        if (currentDirectory == null) {
            currentDirectory = config.getCurrentDirectory();
        }

        if (currentDirectory == null) {
            currentDirectory = ".";
        }
        
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new XMLFilter() );
        fc.setCurrentDirectory(new File(currentDirectory) );
        
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        config.setCurrentDirectory(fc.getCurrentDirectory().getAbsolutePath() );
        
        String file = fc.getSelectedFile().getAbsolutePath();
        updateSelectedFile(file);        
    }


    private static class XMLFilter extends FileFilter {
        
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".xml");
        }
    
        public String getDescription() {
            return "XML Files";
        }
    }    
}