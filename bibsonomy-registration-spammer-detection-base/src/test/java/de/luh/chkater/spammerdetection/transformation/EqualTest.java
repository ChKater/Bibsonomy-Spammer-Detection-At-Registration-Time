package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class EqualTest {

	@Test
	public void test() {
		Equal eq = new Equal();
		
		//test null
		assertEquals(null, eq.apply(null, null));
		assertEquals(null, eq.apply("test", null));
		assertEquals(null, eq.apply(null, "test"));
		
		assertEquals(true, eq.apply("test", "test"));
		assertEquals(false, eq.apply("test", "Test"));
		assertEquals(false, eq.apply("t3est", "test"));

	}

}
