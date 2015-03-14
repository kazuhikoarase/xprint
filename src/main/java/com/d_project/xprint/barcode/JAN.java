package com.d_project.xprint.barcode;

import java.util.ArrayList;
import java.util.List;

/**
 * JAN
 * @author Kazuhiko Arase
 */
public class JAN extends Barcode {

    private static final Double NARROW = new Double(1);
    private static final Double WIDE = new Double(2.5);

    private static final Double L1 = new Double(NARROW.doubleValue() * 1);
    private static final Double L2 = new Double(NARROW.doubleValue() * 2);
    private static final Double L3 = new Double(NARROW.doubleValue() * 3);
    private static final Double L4 = new Double(NARROW.doubleValue() * 4);

    private static final int S1 = 1;
    private static final int S2 = 2;
    private static final int S3 = 3;
    private static final int S4 = 4;
    
    private static final int B1 = 5;
    private static final int B2 = 6;
    private static final int B3 = 7;
    private static final int B4 = 8;

    private static final int EVEN = 0;
    private static final int ODD  = 1;

// num, e/o 
    private static final int[][][] PATTERN = {
        { {S1, B1, S2, B3},  {S3, B2, S1, B1} },
        { {S1, B2, S2, B2},  {S2, B2, S2, B1} },
        { {S2, B2, S1, B2},  {S2, B1, S2, B2} },
        { {S1, B1, S4, B1},  {S1, B4, S1, B1} },
        { {S2, B3, S1, B1},  {S1, B1, S3, B2} },
        { {S1, B3, S2, B1},  {S1, B2, S3, B1} },
        { {S4, B1, S1, B1},  {S1, B1, S1, B4} },
        { {S2, B1, S3, B1},  {S1, B3, S1, B2} },
        { {S3, B1, S2, B1},  {S1, B2, S1, B3} },
        { {S2, B1, S1, B3},  {S3, B1, S1, B2} }
    };

    private static final int[][] OE_PATTERN = {
        {ODD, ODD,  ODD,  ODD,  ODD,  ODD },
        {ODD, ODD,  EVEN, ODD,  EVEN, EVEN},
        {ODD, ODD,  EVEN, EVEN, ODD,  EVEN},
        {ODD, ODD,  EVEN, EVEN, EVEN, ODD },
        {ODD, EVEN, ODD,  ODD,  EVEN, EVEN},
        {ODD, EVEN, EVEN, ODD,  ODD,  EVEN},
        {ODD, EVEN, EVEN, EVEN, ODD,  ODD },
        {ODD, EVEN, ODD,  EVEN, ODD,  EVEN},
        {ODD, EVEN, ODD,  EVEN, EVEN, ODD },
        {ODD, EVEN, EVEN, ODD,  EVEN, ODD }
    };

    public Double[] createPattern() {

        String data = getData();

        if (data.length() == 12 || data.length() == 7) {
            char checkDigit = ModUtil.calcMod10_3W(data);
            data = data + checkDigit;
        }

        List pattern = new ArrayList();

        // quiet
        pattern.add(new Double(NARROW.doubleValue() * 10) );

        // start        
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);

        if (data.length() == 13) {
            createStandard(pattern, data);
        } else if (data.length() == 8) {
            createShort(pattern, data);
        } else {
            throw new RuntimeException("invalid data length " + data);
        }

        // stop
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);

        // quiet
        pattern.add(new Double(NARROW.doubleValue() * 10) );

        return (Double[])pattern.toArray(new Double[pattern.size()]);
    }
    
    private void createStandard(List pattern, String data) {

        int startChar = data.charAt(0) - '0';
        if (startChar < 0 || 9 < startChar) {
            throw new RuntimeException("invalid char " + data + " at 1");
        }            

        for (int i = 1; i < 7; i++) {
            int c = data.charAt(i) - '0';
            if (c < 0 || 9 < c) {
                throw new RuntimeException("invalid char " + data + " at " + (i + 1) );
            }
            int[] pat = PATTERN[c][OE_PATTERN[startChar][i - 1] ];
            for (int p = 0; p < pat.length; p++) {
                addPattern(pattern, pat[p]);
            }
        }

        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);

        for (int i = 7; i < 13; i++) {
            int c = data.charAt(i) - '0';
            if (c < 0 || 9 < c) {
                throw new RuntimeException("invalid char " + data + " at " + (i + 1) );
            }            
            int[] pat = PATTERN[c][EVEN];
            for (int p = pat.length - 1; p >= 0; p--) {
                addPattern(pattern, pat[p]);
            }
        }
    }

    private void createShort(List pattern, String data) {

        for (int i = 0; i < 4; i++) {
            int c = data.charAt(i) - '0';
            if (c < 0 || 9 < c) {
                throw new RuntimeException("invalid char " + data + " at " + (i + 1) );
            }            
            int[] pat = PATTERN[c][ODD];
            for (int p = 0; p < pat.length; p++) {
                addPattern(pattern, pat[p]);
            }
        }

        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);

        for (int i = 4; i < 8; i++) {
            int c = data.charAt(i) - '0';
            if (c < 0 || 9 < c) {
                throw new RuntimeException("invalid char " + data + " at " + (i + 1) );
            }            
            int[] pat = PATTERN[c][EVEN];
            for (int p = pat.length - 1; p >= 0; p--) {
                addPattern(pattern, pat[p]);
            }
        }

    }
    

    private void addPattern(List pattern, int pat) {

        switch(pat) {

        case S1 :
        case B1 :
            pattern.add(L1);
            break;

        case S2 :
        case B2 :
            pattern.add(L2);
            break;

        case S3 :
        case B3 :
            pattern.add(L3);
            break;

        case S4 :
        case B4 :
            pattern.add(L4);
            break;

         default:
            break;
        }
    }

}