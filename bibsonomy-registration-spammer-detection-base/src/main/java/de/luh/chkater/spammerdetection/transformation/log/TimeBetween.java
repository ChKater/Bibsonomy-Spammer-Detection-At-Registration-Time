package de.luh.chkater.spammerdetection.transformation.log;

import java.util.Date;
import java.util.function.BiFunction;

/**
 * 
 * Determine the time between two dates.
 *
 * @author kater
 */
public class TimeBetween implements BiFunction<Date, Date, Double>{

	@Override
	public Double apply(Date first, Date second) {
		
		return new Double(Math.abs(first.getTime() - second.getTime()));
	}

}
