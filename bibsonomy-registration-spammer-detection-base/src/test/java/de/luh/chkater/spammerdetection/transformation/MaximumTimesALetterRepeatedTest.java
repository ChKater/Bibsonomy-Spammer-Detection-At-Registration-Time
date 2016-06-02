package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class MaximumTimesALetterRepeatedTest {

	@Test
	public void test() {
		MaximumTimesALetterRepeated rep = new MaximumTimesALetterRepeated();
		
		assertEquals(null, rep.apply(null));
				
		assertEquals(1, rep.apply("test"), 0);
		assertEquals(3, rep.apply("maschinellesLernen"), 0);
		assertEquals(1, rep.apply("Universität"), 0);
		assertEquals(1, rep.apply("Bibsonomy"), 0);
		assertEquals(1, rep.apply("Informatik"), 0);
		assertEquals(0, rep.apply(""), 0);
	}

}
