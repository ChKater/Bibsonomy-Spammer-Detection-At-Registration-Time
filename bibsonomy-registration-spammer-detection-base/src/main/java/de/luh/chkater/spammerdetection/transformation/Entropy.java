package de.luh.chkater.spammerdetection.transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Calculates the shannon entropy of an string.
 *
 * @author Christian Kater
 */
public class Entropy implements Function<String, Double> {


	@Override
	public Double apply(String word) {
		Map<Character, Integer> wordCount = new HashMap<>();

		if(word == null){
			return null;
		}
		wordCount.clear();
		int length = word.length();

		for (int i = 0; i < length; i++) {
			char symbol = word.charAt(i);
			Integer count = wordCount.get(symbol);
			if (count == null) {
				wordCount.put(symbol, 1);
			} else {
				wordCount.put(symbol, count + 1);
			}
		}
		double entropy = 0;
		for (Integer count : wordCount.values()) {
			double propability = count.doubleValue() / (double) length;
			entropy += (propability * log2(propability));
		}
		return entropy * -1;
	}
	
	private double log2(double x){
		return Math.log(x) / Math.log(2);
	}

}
