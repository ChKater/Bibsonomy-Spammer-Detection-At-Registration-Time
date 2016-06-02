package de.luh.chkater.spammerdetection.transformation;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.BiFunction;

import com.dataiku.geoip.fastgeo.FastGeoIP2;
import com.dataiku.geoip.uniquedb.InvalidDatabaseException;

/**
 * 
 * Transform an date and ip into the local time in the timezone corresponding to the ip.
 *
 * @author kater
 */
public class TransformToLocalTime implements BiFunction<Date, String, Date> {

	FastGeoIP2 geo;

	public TransformToLocalTime(){
		try {
			geo = new FastGeoIP2(new File("FastGeoLite2-City.mmdb"));
		} catch (InvalidDatabaseException e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}

	private static final int BERLIN_RAWOFFSET = 3600000;

	

	@SuppressWarnings("deprecation")
	@Override
	public Date apply(Date origin, String ip) {
		if (origin == null) {
			return null;
		}
		try {
			TimeZone timeZone;
			try {
				timeZone = TimeZone
						.getTimeZone(geo.find(InetAddress.getByName(ip)).getTimezone());
			} catch (Exception e) {
				return null;
			}
			int minutesToAdd = (timeZone.getRawOffset() - BERLIN_RAWOFFSET) / 60000;

			Date newDate = new Date(origin.getTime());
			newDate.setMinutes(newDate.getMinutes() + minutesToAdd);
			return newDate;
		} catch (Exception e) {
			return null;
		}
	}
}
