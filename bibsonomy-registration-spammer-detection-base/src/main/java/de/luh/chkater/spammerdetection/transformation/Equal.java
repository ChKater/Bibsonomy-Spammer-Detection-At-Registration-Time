package de.luh.chkater.spammerdetection.transformation;

import java.util.function.BiFunction;

/**
 * Checks if two String are equal
 *
 * @author Christian Kater
 */
public class Equal implements BiFunction<String, String, Boolean>{

	

	/* (non-Javadoc)
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Boolean apply(String first, String second) {
		if(first == null || second == null){
			return null;
		}
		return first.equals(second);
	}

}
