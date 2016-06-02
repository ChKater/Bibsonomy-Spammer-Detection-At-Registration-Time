package de.luh.chkater.spammerdetection.transformation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Counts the number of unique alphabet characters in an string.
 *
 * @author Christian Kater
 */
public class NumberOfUniqueAlphabetLetters implements Function<String, Double> {

	@Override
	public Double apply(String toInspect) {
		if (toInspect == null) {
			return null;
		}
		toInspect = toInspect.toLowerCase();
		Set<Character> characterCount = new HashSet<>();
		for (int i = 0; i < toInspect.length(); i++) {
			char current = toInspect.charAt(i);
			if (!Character.isAlphabetic(current)) {
				continue;
			}
			characterCount.add(new Character(current));
		}
		return new Double(characterCount.size());
	}

}
