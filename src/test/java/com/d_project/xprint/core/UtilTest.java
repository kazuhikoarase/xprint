package com.d_project.xprint.core;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void test1() {
	
		assertEquals(new BigDecimal("0.0001"), Util.toBigDecimal(0.00005) );
		assertEquals(new BigDecimal("0.0000"), Util.toBigDecimal(0.00004) );
		assertEquals(Util.toBigDecimal(1.00006), Util.toBigDecimal(1.00005) );
		assertEquals(Util.toBigDecimal(1.00016), Util.toBigDecimal(1.00015) );
		assertEquals(Util.toBigDecimal(1.00014), Util.toBigDecimal(1.00013) );

		assertTrue(Util.compare(1, 0) > 0);
		assertTrue(Util.compare(0, 1) < 0);
		assertTrue(Util.compare(1, 1) == 0);
		assertTrue(Util.compare(-1, 0) < 0);
		assertTrue(Util.compare(0, -1) > 0);
		assertTrue(Util.compare(-1, -1) == 0);
		assertTrue(Util.compare(0, 0.00006) < 0);
		assertTrue(Util.compare(0.00006, 0) > 0);
		assertTrue(Util.compare(0, 0.00004) == 0);
		assertTrue(Util.compare(0.00004, 0) == 0);
		
	}
}