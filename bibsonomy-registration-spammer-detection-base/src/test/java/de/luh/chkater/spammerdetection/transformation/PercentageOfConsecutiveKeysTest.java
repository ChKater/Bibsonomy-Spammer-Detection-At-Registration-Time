package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

import de.luh.chkater.spammerdetection.keyboard.DVORAK;
import de.luh.chkater.spammerdetection.keyboard.QwertzDe;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class PercentageOfConsecutiveKeysTest {

	@Test
	public void test() {
		PercentageOfConsecutiveKeys pockFQwertz = new PercentageOfConsecutiveKeys(new QwertzDe().keyToFinger());

		assertEquals(null, pockFQwertz.apply(null));
		assertEquals(null, pockFQwertz.apply("a"));

		assertEquals(0.0, pockFQwertz.apply("Test"), 0.001);
		assertEquals(0.0, pockFQwertz.apply("Bibsonomy"), 0.001);
		assertEquals(1.0 / 11.0, pockFQwertz.apply("Masterarbeit"), 0.001);
		assertEquals(2.0 / 23.0, pockFQwertz.apply("Informatik@Hannover2016:"), 0.001);

		PercentageOfConsecutiveKeys pockHQwertz = new PercentageOfConsecutiveKeys(new QwertzDe().keyToHand());

		assertEquals(null, pockHQwertz.apply(null));
		assertEquals(null, pockHQwertz.apply("a"));

		assertEquals(1.0, pockHQwertz.apply("Test"), 0.001);
		assertEquals(4.0 / 8.0, pockHQwertz.apply("Bibsonomy"), 0.001);
		assertEquals(8.0 / 11.0, pockHQwertz.apply("Masterarbeit"), 0.001);
		assertEquals(9.0 / 23.0, pockHQwertz.apply("Informatik@Hannover2016:"), 0.001);
		
		PercentageOfConsecutiveKeys pockHDvorak = new PercentageOfConsecutiveKeys(new DVORAK().keyToHand());

		assertEquals(null, pockHDvorak.apply(null));
		assertEquals(null, pockHDvorak.apply("a"));

		assertEquals(0.333, pockHDvorak.apply("Test"), 0.001);
		assertEquals(1.0 / 8.0, pockHDvorak.apply("Bibsonomy"), 0.001);
		assertEquals(3.0 / 11.0, pockHDvorak.apply("Masterarbeit"), 0.001);
		assertEquals(7.0 / 23.0, pockHDvorak.apply("Informatik@Hannover2016:"), 0.001);
		

	}

}
