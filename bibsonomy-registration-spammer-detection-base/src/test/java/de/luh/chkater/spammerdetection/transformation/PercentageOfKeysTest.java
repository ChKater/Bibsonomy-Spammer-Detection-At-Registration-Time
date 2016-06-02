package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

import de.luh.chkater.spammerdetection.keyboard.DVORAK;
import de.luh.chkater.spammerdetection.keyboard.KeyBoardLayout;
import de.luh.chkater.spammerdetection.keyboard.QwertyUs;
import de.luh.chkater.spammerdetection.keyboard.QwertzDe;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class PercentageOfKeysTest {

	@Test
	public void test() {

		PercentageOfKeys pokR2 = new PercentageOfKeys(new QwertzDe().keyToRows(), KeyBoardLayout.SECOND_ROW);

		assertEquals(null, pokR2.apply(null));

		assertEquals(0.75, pokR2.apply("Test"), 0.001);
		assertEquals(3.0 / 9.0, pokR2.apply("Bibsonomy"), 0.001);
		assertEquals(7.0 / 12.0, pokR2.apply("Masterarbeit"), 0.001);
		assertEquals(9.0 / 24.0, pokR2.apply("Informatik@Hannover2016:"), 0.001);
				
		PercentageOfKeys pokR3 = new PercentageOfKeys(new QwertzDe().keyToRows(), KeyBoardLayout.THIRD_ROW);

		assertEquals(null, pokR3.apply(null));

		assertEquals(0.25, pokR3.apply("Test"), 0.001);
		assertEquals(1.0 / 9.0, pokR3.apply("Bibsonomy"), 0.001);
		assertEquals(3.0 / 12.0, pokR3.apply("Masterarbeit"), 0.001);
		assertEquals(5.0 / 24.0, pokR3.apply("Informatik@Hannover2016:"), 0.001);
		
	
		PercentageOfKeys pokLFL = new PercentageOfKeys(new QwertzDe().keyToFinger(), KeyBoardLayout.MIDDLE_FINGER_LEFT);
		
		assertEquals(null, pokLFL.apply(null));
		
		assertEquals(0.25, pokLFL.apply("Test"), 0.001);
		assertEquals(0, pokLFL.apply("Bibsonomy"), 0.001);
		assertEquals(2.0 / 12.0, pokLFL.apply("Masterarbeit"), 0.001);
		assertEquals(1.0 / 24.0, pokLFL.apply("Informatik@Hannover2016:"), 0.001);
		
		PercentageOfKeys pokR3Dvorak = new PercentageOfKeys(new DVORAK().keyToRows(), KeyBoardLayout.THIRD_ROW);

		assertEquals(null, pokR3Dvorak.apply(null));

		assertEquals(1.0, pokR3Dvorak.apply("Test"), 0.001);
		assertEquals(5.0 / 9.0, pokR3Dvorak.apply("Bibsonomy"), 0.001);
		assertEquals(8.0 / 12.0, pokR3Dvorak.apply("Masterarbeit"), 0.001);
		assertEquals(12.0 / 24.0, pokR3Dvorak.apply("Informatik@Hannover2016:"), 0.001);
		

	}

}
