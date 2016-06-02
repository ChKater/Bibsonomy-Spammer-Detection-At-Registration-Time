package de.luh.chkater.spammerdetection.transformation;

import java.util.function.Function;

/**
 * Transforms an String into its Proportion of Digits.
 *
 * @author Christian Kater
 */
public class ProportionOfDigits implements Function<String, Double> {

	@Override
	public Double apply(String toInspect) {
		if (toInspect == null || toInspect.length() == 0) {
			return null;
		}
		String digitsOnly = toInspect.replaceAll("[^0-9]", "");
		return new Double((double) digitsOnly.length() / (double) toInspect.length());
	}

}
