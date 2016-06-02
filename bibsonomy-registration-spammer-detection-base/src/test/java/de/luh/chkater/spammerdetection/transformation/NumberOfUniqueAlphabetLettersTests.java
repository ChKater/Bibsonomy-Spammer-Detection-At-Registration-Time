package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class NumberOfUniqueAlphabetLettersTests {

	@Test
	public void test() {
		NumberOfUniqueAlphabetLetters nual = new NumberOfUniqueAlphabetLetters();
		
		assertEquals(null, nual.apply(null));
		
		assertEquals(0, nual.apply(""), 0);
		assertEquals(3, nual.apply("Test"), 0);
		assertEquals(3, nual.apply("TestTest"), 0);
		assertEquals(7, nual.apply("Bibsonomy"), 0);
		assertEquals(8, nual.apply("Masterarbeit"), 0);
		assertEquals(8, nual.apply("Masterarbeit@1234"), 0);
	
		
	}

}
