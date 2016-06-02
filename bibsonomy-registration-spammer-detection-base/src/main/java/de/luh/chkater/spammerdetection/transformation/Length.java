package de.luh.chkater.spammerdetection.transformation;

import java.util.function.Function;

/**
 * 
 * Transform an String into his length
 *
 * @author kater
 */
public class Length implements Function<String, Double> {

	@Override
	public Double apply(String toInspect) {
		if(toInspect == null){
			return null;
		}
		return new Double(toInspect.length());
	}

}
