package com.d_project.xprint.core;

import java.awt.Color;
import java.math.BigDecimal;

/**
 * Util
 * @author Kazuhiko Arase
 */
public final class Util {

    private Util() {
    }

    public static boolean isEmpty(String s) {
    	return s == null || s.length() == 0;
    }
    
    public static Color parseColor(String s) {
        try {
            
            if (s.charAt(0) != '#') {
            	throw new IllegalArgumentException(s);
            }
            
            return new Color(
                Integer.parseInt(s.substring(1, 3), 16),
                Integer.parseInt(s.substring(3, 5), 16),
                Integer.parseInt(s.substring(5, 7), 16) );
            
        } catch(Exception e) {
            return Color.black;
        }
    }

	public static double parseNumber(String s) {

		try {
			if (isEmpty(s) ) {
				return 0;
			} else if (s.endsWith("mm") ) {
				return Double.parseDouble(s.substring(0, s.length() - 2) ) / 25.4 * 72;
			} else if (s.endsWith("cm") ) {
				return Double.parseDouble(s.substring(0, s.length() - 2) ) / 2.54 * 72;
			} else if (s.endsWith("in") ) {
				return Double.parseDouble(s.substring(0, s.length() - 2) ) * 72;
			} else if (s.endsWith("pt") ) {
				return Double.parseDouble(s.substring(0, s.length() - 2) );
			} else {
				return Double.parseDouble(s);
			}

		} catch(Exception e) {
			return 0;
		}
	}
    
    public static int compare(double v1, double v2) {
    	BigDecimal b1 = toBigDecimal(v1);
    	BigDecimal b2 = toBigDecimal(v2);
    	return b1.compareTo(b2);
    }

    public static BigDecimal toBigDecimal(double value) {
    	return new BigDecimal(value).setScale(4, BigDecimal.ROUND_HALF_UP);
    }
}
