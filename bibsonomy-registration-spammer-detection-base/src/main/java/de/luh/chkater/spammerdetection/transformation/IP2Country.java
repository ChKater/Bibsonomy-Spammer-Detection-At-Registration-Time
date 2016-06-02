package de.luh.chkater.spammerdetection.transformation;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.dataiku.geoip.fastgeo.FastGeoIP2;
import com.dataiku.geoip.uniquedb.InvalidDatabaseException;

/**
 * Transforms an IP-Adress into the corresponding two letter ISO 3166 code of the
 * corresponding country.
 *
 * @author Christian Kater
 */
public class IP2Country implements Function<String, String> {
	
	public static final List<String> countryIsoCodes = Arrays.asList("AF", "AX", "AL", "DZ", "AS", "AD", "AO", "AI", "AQ", "AG", "AR", "AM", "AW", "AU", "AT",
			"AZ", "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO", "BA", "BW", "BV", "BR", "VG",
			"IO", "BN", "BG", "BF", "BI", "KH", "CM", "CA", "CV", "KY", "CF", "TD", "CL", "CN", "HK", "MO", "CX",
			"CC", "CO", "KM", "CG", "CD", "CK", "CR", "CI", "HR", "CU", "CY", "CZ", "DK", "DJ", "DM", "DO", "EC",
			"EG", "SV", "GQ", "ER", "EE", "ET", "FK", "FO", "FJ", "FI", "FR", "GF", "PF", "TF", "GA", "GM", "GE",
			"DE", "GH", "GI", "GR", "GL", "GD", "GP", "GU", "GT", "GG", "GN", "GW", "GY", "HT", "HM", "VA", "HN",
			"HU", "IS", "IN", "ID", "IR", "IQ", "IE", "IM", "IL", "IT", "JM", "JP", "JE", "JO", "KZ", "KE", "KI",
			"KP", "KR", "KW", "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MK", "MG", "MW", "MY",
			"MV", "ML", "MT", "MH", "MQ", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS", "MA", "MZ",
			"MM", "NA", "NR", "NP", "NL", "AN", "NC", "NZ", "NI", "NE", "NG", "NU", "NF", "MP", "NO", "OM", "PK",
			"PW", "PS", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RE", "RO", "RU", "RW", "BL",
			"SH", "KN", "LC", "MF", "PM", "VC", "WS", "SM", "ST", "SA", "SN", "RS", "SC", "SL", "SG", "SK", "SI",
			"SB", "SO", "ZA", "GS", "SS", "ES", "LK", "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ",
			"TH", "TL", "TG", "TK", "TO", "TT", "TN", "TR", "TM", "TC", "TV", "UG", "UA", "AE", "GB", "US", "UM",
			"UY", "UZ", "VU", "VE", "VN", "VI", "WF", "EH", "YE", "ZM", "ZW");
	
	
	FastGeoIP2 geo;
	
	public IP2Country(){
		try {
			geo = new FastGeoIP2(new File("FastGeoLite2-City.mmdb"));
		} catch (InvalidDatabaseException e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}


	@Override
	public String apply(String ip) {
		if (ip == null) {
			return null;
		}
		String country;
		try {
			long start = System.currentTimeMillis();
			country = geo.find(InetAddress.getByName(ip)).getCountryCode();
			long end = System.currentTimeMillis();
			long time = end - start;
		} catch (Exception e) {
			return null;
		}
		if (country == null || country.length() < 2) {
			return null;
		}
		return country;
	}

}
