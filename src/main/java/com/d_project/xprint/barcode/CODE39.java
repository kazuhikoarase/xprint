package com.d_project.xprint.barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CODE39
 * @author Kazuhiko Arase
 */
public class CODE39 extends Barcode {

    private static final Double NARROW = new Double(1);
    private static final Double WIDE = new Double(2.5);

    private static final int NS = 1;
    private static final int NB = 2;
    private static final int WS = 3;
    private static final int WB = 4;
    
    private static Map map;
    
    static {
    
        map = new HashMap();
    
        map.put("0", new int[]{NB, NS, NB, WS, WB, NS, WB, NS, NB}); // 0 
        map.put("1", new int[]{WB, NS, NB, WS, NB, NS, NB, NS, WB}); // 1
        map.put("2", new int[]{NB, NS, WB, WS, NB, NS, NB, NS, WB}); // 2
        map.put("3", new int[]{WB, NS, WB, WS, NB, NS, NB, NS, NB}); // 3
        map.put("4", new int[]{NB, NS, NB, WS, WB, NS, NB, NS, WB}); // 4
        map.put("5", new int[]{WB, NS, NB, WS, WB, NS, NB, NS, NB}); // 5
        map.put("6", new int[]{NB, NS, WB, WS, WB, NS, NB, NS, NB}); // 6
        map.put("7", new int[]{NB, NS, NB, WS, NB, NS, WB, NS, WB}); // 7
        map.put("8", new int[]{WB, NS, NB, WS, NB, NS, WB, NS, NB}); // 8
        map.put("9", new int[]{NB, NS, WB, WS, NB, NS, WB, NS, NB}); // 9
        
        map.put("A", new int[]{WB, NS, NB, NS, NB, WS, NB, NS, WB}); // A
        map.put("B", new int[]{NB, NS, WB, NS, NB, WS, NB, NS, WB}); // B
        map.put("C", new int[]{WB, NS, WB, NS, NB, WS, NB, NS, NB}); // C
        map.put("D", new int[]{NB, NS, NB, NS, WB, WS, NB, NS, WB}); // D
        map.put("E", new int[]{WB, NS, NB, NS, WB, WS, NB, NS, NB}); // E
        map.put("F", new int[]{NB, NS, WB, NS, WB, WS, NB, NS, NB}); // F
        map.put("G", new int[]{NB, NS, NB, NS, NB, WS, WB, NS, WB}); // G
        map.put("H", new int[]{WB, NS, NB, NS, NB, WS, WB, NS, NB}); // H
        map.put("I", new int[]{NB, NS, WB, NS, NB, WS, WB, NS, NB}); // I
        map.put("J", new int[]{NB, NS, NB, NS, WB, WS, WB, NS, NB}); // J
        map.put("K", new int[]{WB, NS, NB, NS, NB, NS, NB, WS, WB}); // K
        map.put("L", new int[]{NB, NS, WB, NS, NB, NS, NB, WS, WB}); // L
        map.put("M", new int[]{WB, NS, WB, NS, NB, NS, NB, WS, NB}); // M
        map.put("N", new int[]{NB, NS, NB, NS, WB, NS, NB, WS, WB}); // N
        map.put("O", new int[]{WB, NS, NB, NS, WB, NS, NB, WS, NB}); // O
        map.put("P", new int[]{NB, NS, WB, NS, WB, NS, NB, WS, NB}); // P
        map.put("Q", new int[]{NB, NS, NB, NS, NB, NS, WB, WS, WB}); // Q
        map.put("R", new int[]{WB, NS, NB, NS, NB, NS, WB, WS, NB}); // R
        map.put("S", new int[]{NB, NS, WB, NS, NB, NS, WB, WS, NB}); // S
        map.put("T", new int[]{NB, NS, NB, NS, WB, NS, WB, WS, NB}); // T
        map.put("U", new int[]{WB, WS, NB, NS, NB, NS, NB, NS, WB}); // U
        map.put("V", new int[]{NB, WS, WB, NS, NB, NS, NB, NS, WB}); // V
        map.put("W", new int[]{WB, WS, WB, NS, NB, NS, NB, NS, NB}); // W
        map.put("X", new int[]{NB, WS, NB, NS, WB, NS, NB, NS, WB}); // X
        map.put("Y", new int[]{WB, WS, NB, NS, WB, NS, NB, NS, NB}); // Y
        map.put("Z", new int[]{NB, WS, WB, NS, WB, NS, NB, NS, NB}); // Z
        
        map.put("-", new int[]{NB, WS, NB, NS, NB, NS, WB, NS, WB}); // -
        map.put(".", new int[]{WB, WS, NB, NS, NB, NS, WB, NS, NB}); // .
        map.put(" ", new int[]{NB, WS, WB, NS, NB, NS, WB, NS, NB}); // (SPC)
        map.put("$", new int[]{NB, WS, NB, WS, NB, WS, NB, NS, NB}); // $
        map.put("/", new int[]{NB, WS, NB, WS, NB, NS, NB, WS, NB}); // /
        map.put("+", new int[]{NB, WS, NB, NS, NB, WS, NB, WS, NB}); // +
        map.put("%", new int[]{NB, NS, NB, WS, NB, WS, NB, WS, NB}); // %
        map.put("*", new int[]{NB, WS, NB, NS, WB, NS, WB, NS, NB}); // *
    
    }
    
    private static final String CODE39_START_CHAR = "*";
    private static final String CODE39_STOP_CHAR  = "*";
    private static final String CODE39_EXTRAS = "-. $/+%";

    
    private void add(List pattern, String s) {

        int[] pat = (int[])map.get(s);

        for (int i = 0; i < pat.length; i++) {

            switch (pat[i]) {

            case NS:
            case NB:
                pattern.add(NARROW);
                break;

            case WS:
            case WB:
                pattern.add(WIDE);
                break;

            default:
                break;
            }
        }
    }
    
    public Double[] createPattern() {

        String data = getData();
/*
        char checkDigit = ModUtil.calcMod43(data);
        data = data + checkDigit;
*/
   
        List pattern = new ArrayList();

        // quiet
        pattern.add(new Double(NARROW.doubleValue() * 10) );

        // start        
        add(pattern, CODE39_START_CHAR);

        for (int i = 0; i < data.length(); i++) {

            pattern.add(NARROW);

            if (charToInt(data.charAt(i) ) == -1) {
                throw new RuntimeException("illegal char " + data + " at " + (i + 1) );
            }

            add(pattern, String.valueOf(data.charAt(i) ) );
        }

        // stop
        pattern.add(NARROW);
        add(pattern, CODE39_STOP_CHAR);

        // quiet
        pattern.add(new Double(NARROW.doubleValue() * 10) );

        return (Double[])pattern.toArray(new Double[pattern.size()]);
    }
    
    public static int charToInt(int c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('A' <= c && c <= 'Z') {
            return c - 'A' + 10;
        } else if (CODE39_EXTRAS.indexOf(c) != -1) {
            return CODE39_EXTRAS.indexOf(c) + 36;
        } else {
            return -1;
        }
    }
    
    public static int intToChar(int num) {
        if (num < 0) {
            return -1;
        } else if (num < 10) {
            return (char)(num + '0');
        } else if (num < 36) {
            return (char)(num - 10 + 'A');
        } else if (num < 43) {
            return CODE39_EXTRAS.charAt(num - 36);
        } else {
            return -1;
        }
    }
}
