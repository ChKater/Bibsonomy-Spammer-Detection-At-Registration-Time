package de.luh.chkater.spammerdetection.utility;

public class IPSelector {

	public static String selectIPAdress(String ipAdresses){
		if(ipAdresses == null){
			return null;
		}
		String[] ips = ipAdresses.split(",");
		if(ips.length == 1 || ips.length == 2){
			return ips[0];
		} else if(ips.length == 3){
			return ips[1];
		}
		return null;
	}
}
