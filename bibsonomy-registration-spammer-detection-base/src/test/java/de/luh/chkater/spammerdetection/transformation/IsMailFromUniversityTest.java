package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class IsMailFromUniversityTest {

	@Test
	public void test() {
		IsMailFromUniversity ismfu = new IsMailFromUniversity();
		
		assertEquals(null, ismfu.apply(null));
		assertEquals(false, ismfu.apply("test@gmail.com"));
		assertEquals(false, ismfu.apply("test@gmx.de"));
		assertEquals(false, ismfu.apply("test@web.de"));
		assertEquals(true, ismfu.apply("test@kbs.uni-hannover.de"));
		assertEquals(true, ismfu.apply("test@ibr.cs.tu-bs.de"));
		assertEquals(true, ismfu.apply("test@tu-berlin.de"));
	}

}
