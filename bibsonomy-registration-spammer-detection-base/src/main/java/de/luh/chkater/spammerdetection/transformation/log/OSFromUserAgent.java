package de.luh.chkater.spammerdetection.transformation.log;

import java.util.function.Function;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * Extract the operating system from an user agent
 *
 * @author kater
 */
public class OSFromUserAgent implements Function<UserAgent, String>{

	
	@Override
	public String apply(UserAgent userAgent) {
		if(userAgent == null){
			return null;
		}
		String os = userAgent.getOperatingSystem().getGroup().getName();
		if(os == null){
			return null;
		}
		return os;
	}

}
