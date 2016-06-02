package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class CountInStringTest {

	@Test
	public void test() {
		CountInString cis = new CountInString();
		
		//test null
		assertEquals(null, cis.apply(null, null));
		assertEquals(null, cis.apply(null, "[^0-9]"));
		
		//test empty string
		assertEquals(0.0, cis.apply("", "[^0-9]").doubleValue(), 0);
		assertEquals(0.0, cis.apply("", "[^a-zA-Z]").doubleValue(), 0);
		
		assertEquals(4.0, cis.apply("1234Test", "[^0-9]").doubleValue(), 0);
		assertEquals(4.0, cis.apply("Test1234", "[^0-9]").doubleValue(), 0);
		assertEquals(4.0, cis.apply("12Test34", "[^0-9]").doubleValue(), 0);
		assertEquals(1.0, cis.apply("T1st", "[^0-9]").doubleValue(), 0);
		assertEquals(3.0, cis.apply("T1st@T1est4All@", "[^0-9]").doubleValue(), 0);
		
		assertEquals(4.0, cis.apply("1234Test", "[^a-zA-Z]").doubleValue(), 0);
		assertEquals(4.0, cis.apply("Test1234", "[^a-zA-Z]").doubleValue(), 0);
		assertEquals(4.0, cis.apply("12Test34", "[^a-zA-Z]").doubleValue(), 0);
		assertEquals(3.0, cis.apply("T1st", "[^a-zA-Z]").doubleValue(), 0);
		assertEquals(10.0, cis.apply("T1st@T1est4All@", "[^a-zA-Z]").doubleValue(), 0);
		
	}

}
