package de.luh.chkater.spammerdetection.transformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.net.InternetDomainName;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Transform a user into a boolean, which holds true whenever the user has an
 * E-Mail from an University. Otherwise false. The top level domains from the
 * universities are from https://github.com/Hipo/university-domains-list. An
 * university email has the form *@*.<univerity tld>.
 *
 * @author Christian Kater
 */
public class IsMailFromUniversity implements Function<String, Boolean> {

	private static Set<String> universityDomains = readUniversityDomainsFromJSON("world_universities_and_domains.json");

	@Override
	public Boolean apply(String email) {
		if(email == null){
			return null;
		}
		String[] mailParts = email.split("@");
		if (mailParts.length != 2) {
			return null;
		}
		String topDomain;
		try {
			topDomain = InternetDomainName.from(mailParts[1]).topPrivateDomain().toString();
		} catch (Exception e) {
			return null;
		}
		return new Boolean(universityDomains.contains(topDomain));
	}

	private static Set<String> readUniversityDomainsFromJSON(String fileName) {
		Set<String> domains = new HashSet<>();
		JsonArray universities = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("world_universities_and_domains.json")));
			Gson gson = new Gson();
			universities = gson.fromJson(br, JsonArray.class);
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		for (JsonElement university : universities) {
			try {
				String domain = university.getAsJsonObject().get("domain").getAsString();
				domains.add(InternetDomainName.from(domain).topPrivateDomain().toString());
			} catch (Exception e) {
				continue;
			}
		}
		return domains;
	}
}
