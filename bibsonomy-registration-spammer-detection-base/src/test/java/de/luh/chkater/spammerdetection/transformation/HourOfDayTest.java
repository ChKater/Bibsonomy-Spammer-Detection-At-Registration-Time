package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class HourOfDayTest {

	@Test
	public void test() {
		HourOfDay ofd = new HourOfDay();
		
		assertEquals(null, ofd.apply(null));
		
		assertEquals("13", ofd.apply(new Date(116, 11, 11, 13, 59)));
		assertEquals("23", ofd.apply(new Date(116, 11, 11, 23, 59)));
	}

}
