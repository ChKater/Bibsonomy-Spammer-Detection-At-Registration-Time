package de.luh.chkater.spammerdetection.transformation;

import java.util.Map;
import java.util.function.Function;

/**
 * 
 * Counts the number of consecutive keys related to the hand, finger or row. 
 *
 * @author kater
 */
public class PercentageOfConsecutiveKeys implements Function<String, Double> {

	private Map<Character, Integer> keyToCountable;

	public PercentageOfConsecutiveKeys(Map<Character, Integer> keyToCountable) {
		super();
		this.keyToCountable = keyToCountable;
	}

	@Override
	public Double apply(String content) {
		// TODO Auto-generated method stub
		if (content == null || content.length() < 2) {
			return null;
		}
		content = content.toUpperCase();
		Integer prev = null;
		int start = 0;
		//skip special chars
		while (prev == null && start < content.length()) {
			prev = keyToCountable.get(content.charAt(start));
			start++;
		}

		double count = 0;
		for (int i = start; i < content.length(); i++) {
			Integer current = keyToCountable.get(content.charAt(i));
			if (current == null) {
				continue;
			}
			if (current.equals(prev)) {
				count++;
			}
			prev = current;
		}
		return new Double(count / (content.length() - 1));
	}

}
