package com.d_project.xprint.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Config
 * @author Kazuhiko Arase
 */
public class Config {

    private static final String CONFIG_FILE_NAME = "xprint_pageviewer.properties";
    private static final String CONFIG_KEY_CURRENT_DIRECTORY = "CurrentDirectory";
    private static final String CONFIG_KEY_FORM_FILE = "FormFile";
    private static final String CONFIG_KEY_DATA_FILE = "DataFile";

    private static Config config = new Config();
    
    public static Config getInstance() {
        return config;
    }

    private String currentDirectory;

    private List templateFiles;
    private List dataFiles;
    
    private Config() {
        dataFiles = new ArrayList();
        templateFiles = new ArrayList();
    }

    public void clearDataFiles() {
        dataFiles.clear();
    }

    public void addDataFile(String dataFile) {
        dataFiles.add(dataFile);
    }

    public String getDataFile(int index) {
        return (String)dataFiles.get(index);
    }
    
    public int getDataFileCount() {
        return dataFiles.size();
    }

    public void clearFormFiles() {
        templateFiles.clear();
    }
        
    public void addFormFile(String templateFile) {
        templateFiles.add(templateFile);
    }

    public String getFormFile(int index) {
        return (String)templateFiles.get(index);
    }
    
    public int getFormFileCount() {
        return templateFiles.size();
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }
    
    public void load() {

        Properties props = new Properties();

        try {
            File propFile = new File(System.getProperty("user.home"), CONFIG_FILE_NAME);
            InputStream in = new BufferedInputStream(new FileInputStream(propFile) );
            try {
                props.load(in);
            } finally {
                in.close();
            }
        } catch (Exception e) {
        }

        setCurrentDirectory(props.getProperty(CONFIG_KEY_CURRENT_DIRECTORY) );

        clearFormFiles();
        for (int i = 0; props.getProperty(CONFIG_KEY_FORM_FILE + "." + i) != null; i++) {
            addFormFile(props.getProperty(CONFIG_KEY_FORM_FILE + "." + i) );
        }        

        clearDataFiles();
        for (int i = 0; props.getProperty(CONFIG_KEY_DATA_FILE + "." + i) != null; i++) {
            addDataFile(props.getProperty(CONFIG_KEY_DATA_FILE + "." + i) );
        }        
    }

    public void save() {

        // new preferences for store.
        Properties props = new Properties();

        if (getCurrentDirectory() != null) {
            props.setProperty(CONFIG_KEY_CURRENT_DIRECTORY, getCurrentDirectory() );
        }

        for (int i = 0; i < getFormFileCount(); i++) {
            props.setProperty(CONFIG_KEY_FORM_FILE + "." + i, getFormFile(i) );
        }

        for (int i = 0; i < getDataFileCount(); i++) {
            props.setProperty(CONFIG_KEY_DATA_FILE + "." + i, getDataFile(i) );
        }

        try {

            File propFile = new File(System.getProperty("user.home"), CONFIG_FILE_NAME);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(propFile) );

            try {
                props.store(out, "xprint");
            } finally {
                out.close();
            }
        } catch (Exception e) {
        }
    }

}
