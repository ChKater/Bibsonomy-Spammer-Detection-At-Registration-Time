package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class DayOfWeekTest {

	@Test
	public void test() {
		DayOfWeek dow = new DayOfWeek();
		
		assertEquals(null, dow.apply(null));
		
		assertEquals("0", dow.apply(new Date(116, 03, 24)));
		assertEquals("1", dow.apply(new Date(116, 01, 29)));
		assertEquals("2", dow.apply(new Date(116, 03, 19)));
		assertEquals("3", dow.apply(new Date(116, 03, 13)));
		assertEquals("4", dow.apply(new Date(116, 03, 07)));
		assertEquals("5", dow.apply(new Date(117, 03, 14)));
		assertEquals("6", dow.apply(new Date(115, 02, 28)));
	}

}
