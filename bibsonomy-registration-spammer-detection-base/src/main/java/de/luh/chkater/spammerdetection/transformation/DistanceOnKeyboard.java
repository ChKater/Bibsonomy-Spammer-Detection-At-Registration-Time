package de.luh.chkater.spammerdetection.transformation;

import java.util.Map;
import java.util.function.Function;

import org.bibsonomy.common.Pair;


/**
 * Calculate the Distance the used moved his finger for typing a string with one finger. It is assumed that a key have a size of 1 x 1
 * @author Christian Kater
 */
public class DistanceOnKeyboard implements Function<String, Double>{

	
	private Map<Character, Pair<Double, Double>> keytoPosition;
	
	
	public DistanceOnKeyboard(Map<Character, Pair<Double, Double>> keytoPosition) {
		super();
		this.keytoPosition = keytoPosition;

	}

	@Override
	public Double apply(String content) {
		if(content == null || content.length() < 2){
			return null;
		}
		Pair<Double, Double> prevPos = null;
		int start = 0;
		
		while(prevPos != null && start < content.length()){
			prevPos = keytoPosition.get(content.charAt(start));
			start++;
		}
	
		double distanceTotal = 0;
		for (int i = start + 1; i < content.length(); i++) {
			Pair<Double, Double> currentPos = keytoPosition.get(content.charAt(i));
			if(currentPos == null){
				continue;
			}
			double distance = distanceBetween(prevPos, currentPos);
			prevPos = currentPos;
			distanceTotal += distance;
		}
		return distanceTotal;
	}
	
	private double distanceBetween(Pair<Double, Double> p1, Pair<Double, Double> p2) {
		if(p1 == null || p2 == null){
			return 0.0;
		}
		double diffX = p1.getFirst()- p2.getFirst();
		double diffY = p1.getSecond() - p2.getSecond();
		return Math.sqrt(diffX * diffX + diffY * diffY);
	}

	
}
