package com.d_project.pdf;


/**
 * Util
 * @author Kazuhiko Arase
 */
public class Util {

	private Util() {
	}
	
	public static String format(long num, int digit) {
		String s = String.valueOf(num);
		while (s.length() < digit) {
			s = "0" + s;
		}
		return s;
	}

    public static char toHexadecimalChar(int b) {
		if (0 <= b && b < 10) {
			return (char)('0' + b);
		} else if (10 <= b && b < 16) {
			return (char)('A' + b - 10);
		}
		throw new Error();
	}
}