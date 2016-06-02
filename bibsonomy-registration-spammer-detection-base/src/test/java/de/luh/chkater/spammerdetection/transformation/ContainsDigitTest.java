package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class ContainsDigitTest {

	@Test
	public void test() {
		ContainsDigit cd = new ContainsDigit();
		
		//test null 
		assertEquals(null, cd.apply(null));
		
		//test empty string
		assertEquals(false, cd.apply(""));
		
		assertEquals(true, cd.apply("Test1234"));
		assertEquals(true, cd.apply("12Test34"));
		assertEquals(true, cd.apply("1234Test"));
		
		assertEquals(false, cd.apply("Test"));
		assertEquals(false, cd.apply("test"));
		assertEquals(false, cd.apply("Test@!"));
	
		
		
	}

}
