package de.luh.chkater.spammerdetection.transformation;

import java.util.Date;
import java.util.function.Function;


/**
 * 
 * Returns the DayOfWeek from an date.
 *
 * @author Christian Kater
 */
public class DayOfWeek implements Function<Date, String>{

	@SuppressWarnings("deprecation")
	@Override
	public String apply(Date date) {
		if(date == null){
			return null;
		}
		return String.valueOf(date.getDay());
	}

}
