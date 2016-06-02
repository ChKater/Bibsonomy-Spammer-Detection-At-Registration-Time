package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class LengthTest {

	@Test
	public void test() {
		Length l = new Length();
		
		assertEquals(null, l.apply(null));
		
		assertEquals(0, l.apply(""), 0);
		assertEquals(1, l.apply("t"), 0);
		assertEquals(2, l.apply("te"), 0);
		assertEquals(3, l.apply("tes"), 0);
		assertEquals(4, l.apply("test"), 0);
	}

}
