package de.luh.chkater.spammerdetection.transformation;

import java.util.function.BiFunction;

/**
 * 
 * Counts the number of characters in an String after removing a certain pattern.
 *
 * @author Christian Kater
 */
public class CountInString implements BiFunction<String, String, Double>{

	@Override
	public Double apply(String word, String replacePattern) {
		if(word == null){
			return null;
		}
		word = word.replaceAll(replacePattern, "");
		
		return new Double(word.length());
	}
}
