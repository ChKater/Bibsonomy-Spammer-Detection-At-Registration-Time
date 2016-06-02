package de.luh.chkater.spammerdetection.transformation;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 
 * Consists the string of digits
 *
 * @author Christian Kater
 */
public class ContainsDigit implements Function<String, Boolean> {

	private static Pattern digitPattern = Pattern.compile("(.)*(\\d)(.)*");

	@Override
	public Boolean apply(String toInspect) {
		if(toInspect == null){
			return null;
		}
		return new Boolean(digitPattern.matcher(toInspect).matches());
	}

}
