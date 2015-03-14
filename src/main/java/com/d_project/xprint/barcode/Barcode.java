package com.d_project.xprint.barcode;

/**
 * Barcode
 * @author Kazuhiko Arase
 */
public abstract class Barcode {
    
    private Double[] pattern;
    private double patternLength;
    private String data;
    private boolean valid;
    
    protected Barcode() {
    }

    public void setData(String data) {
        if (!data.equals(this.data) ) {
            this.data = data;
            this.valid = false;
        }
    }
    
    public String getData() {
        return data;
    }

    public double getPatternLength() {
        preparePattern();
        return patternLength;               
    }
    
    public Double[] getPattern() {
        preparePattern();
        return pattern;
    }
    
    private void preparePattern() {

        if (!valid) {

            Double[] pattern = createPattern();

            double patternLength = 0;

            for (int i = 0; i < pattern.length; i++) {
                patternLength += pattern[i].doubleValue();
            }

            this.pattern = pattern;
            this.patternLength = patternLength;

            valid = true;
            
        }
    }    
    
    public abstract Double[] createPattern();
    
    public static Barcode newInstance(String type) {
        if (BarcodeTypes.ITF.equals(type) ) {
            return new ITF();
        } else if (BarcodeTypes.JAN.equals(type) ) {
            return new JAN();
        } else if (BarcodeTypes.CODE39.equals(type) ) {
            return new CODE39();
        } else {
            return new JAN();
        }
    }
    
}
