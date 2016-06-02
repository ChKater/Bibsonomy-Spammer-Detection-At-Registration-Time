package de.luh.chkater.spammerdetection.transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Transform an String into the max number of repitetion of an character.
 *
 * @author Christian Kater
 */
public class MaximumTimesALetterRepeated implements Function<String, Double>{

	@Override
	public Double apply(String toInspect) {
		if(toInspect == null){
			return null;
		}
		toInspect = toInspect.toLowerCase();
		Map<Character, Integer> characterCount = new HashMap<>();
		for (int i = 0; i < toInspect.length(); i++) {
			Character current = toInspect.charAt(i);
			Integer currentCount = characterCount.get(current);
			if(currentCount == null){
				//lokking for repeated letters. First occurence is not a repetition.
				characterCount.put(current, 0);
			} else{
				characterCount.put(current, currentCount + 1);
			}
		}
		int max = 0;
		for (Character character : characterCount.keySet()) {
			Integer count = characterCount.get(character);
			if(count > max){
				 max = count;
			}
		}
		return new Double(max);
	}
}
