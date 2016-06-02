package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class NumberOfStartingDigitsTest {

	@Test
	public void test() {
		NumberOfStartingDigits nosd = new NumberOfStartingDigits();
		
		assertEquals(null, nosd.apply(null));
		
		assertEquals(0, nosd.apply(""), 0);
		assertEquals(1, nosd.apply("1Test"), 0);
		assertEquals(2, nosd.apply("12Test"), 0);
		assertEquals(3, nosd.apply("123Test"), 0);
		assertEquals(4, nosd.apply("1234Test"), 0);
		assertEquals(2, nosd.apply("12Test34"), 0);
		assertEquals(1, nosd.apply("1Te2st34"), 0);
		
	}

}
