package de.luh.chkater.spammerdetection.transformation;

import java.util.Map;
import java.util.function.Function;

/**
 * 
 * Gives the percentage of keys in a row, hand or finger in an string. 
 *
 * @author Christian Kater
 */
public class PercentageOfKeys implements Function<String, Double>{

	private Map<Character, Integer> keyToCountable;
	private int toCount;

	public PercentageOfKeys(Map<Character, Integer> keyToCountable, int toCount) {
		super();
		this.keyToCountable = keyToCountable;
		this.toCount = toCount;
	}

	@Override
	public Double apply(String content) {
		if(content == null){
			return null;
		}
		content = content.toUpperCase();
		double count = 0;
		for (int i = 0; i < content.length(); i++) {
			char charAt = content.charAt(i);
			Integer countable = keyToCountable.get(charAt);
			if(countable != null && countable == toCount){
				count++;
			}
		}
		return new Double((double) count / (double) content.length());
	}

}
