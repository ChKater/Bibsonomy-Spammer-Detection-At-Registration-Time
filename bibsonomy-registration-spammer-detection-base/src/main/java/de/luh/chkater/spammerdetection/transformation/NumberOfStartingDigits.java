package de.luh.chkater.spammerdetection.transformation;

import java.util.function.Function;

/**
 * Count the number of starting digits in an String
 *
 * @author Christian Kater
 */
public class NumberOfStartingDigits implements Function<String, Double> {

	@Override
	public Double apply(String toInspect) {
		if(toInspect == null){
			return null;
		}
		int i = 0;
		while (i < toInspect.length() && Character.isDigit(toInspect.charAt(i))) {
			i++;
		}
		return new Double(i);

	}

}
