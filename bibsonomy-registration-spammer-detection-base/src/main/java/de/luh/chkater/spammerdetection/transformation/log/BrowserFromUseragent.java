package de.luh.chkater.spammerdetection.transformation.log;

import java.util.function.Function;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * 
 * Extract the browser from the user agent.
 *
 * @author Christian Kater
 */
public class BrowserFromUseragent implements Function<UserAgent, String>{

	@Override
	public String apply(UserAgent userAgent) {
		if(userAgent == null){
			return null;
		}
		String browser = userAgent.getBrowser().getGroup().getName();
		if(browser == null){
			return null;
		}
		return browser;
	}

}
