package de.luh.chkater.spammerdetection.transformation;

import java.util.Date;
import java.util.function.Function;

/**
 * 
 * Transforms an date into to corresponding hour of the day
 *
 * @author Christian Kater
 */
public class HourOfDay implements Function<Date, String>{

	@SuppressWarnings("deprecation")
	@Override
	public String apply(Date date) {
		if(date == null){
			return null;
		}
		return String.valueOf(date.getHours());
	}

}
