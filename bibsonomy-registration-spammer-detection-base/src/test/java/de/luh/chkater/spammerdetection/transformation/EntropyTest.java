package de.luh.chkater.spammerdetection.transformation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class EntropyTest {

	@Test
	public void test() {
		Entropy entropy = new Entropy();
		
		assertEquals(null, entropy.apply(null));
		
		assertEquals(2, entropy.apply("Test"), 0.001);
		assertEquals(2.9183, entropy.apply("Masterarbeit"), 0.001);
		assertEquals(3.32193, entropy.apply("Informatik"), 0.001);
		assertEquals(3.27761, entropy.apply("Lorem ipsum"), 0.001);
		assertEquals(0.98523, entropy.apply("1100101"), 0.001);
		assertEquals(2.80735, entropy.apply("Entropy"), 0.001);
		
		
	}

}
