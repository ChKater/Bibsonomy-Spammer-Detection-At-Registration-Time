package de.luh.chkater.spammerdetection.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.ibm.icu.text.SimpleDateFormat;


/**
 * Reads the logged regestrations from an tsv file
 */
public class LoggedRegistrationReader {
	
	/** The log date formatter. */
	private static SimpleDateFormat logDateFormatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

	/** The registrations. */
	private static List<LoggedRegistration> registrations = null;
	
	/**
	 * Gets the logged registrations.
	 *
	 * @return the logged registrations
	 */
	public static List<LoggedRegistration> getLoggedRegistrations(){
		if(registrations == null){
			initList();
		}
		return registrations;
	}
	
	/**
	 * Gets the registrations.
	 *
	 * @param file the file
	 * @return the registrations
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	private static List<LoggedRegistration> getRegistrations(File file) throws IOException, ParseException{
		List<LoggedRegistration> registrations = new LinkedList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		//skip header
		String line = br.readLine();
		
		while((line = br.readLine()) != null){
			String[] split = line.split("\t");
			LoggedRegistration reg = new LoggedRegistration();
			reg.setActivation(logDateFormatter.parse(split[0]));
			reg.setIp(split[1]);
			reg.setNumberOfErrors(Integer.parseInt(split[2]));
			reg.setReferer(split[3]);
			reg.setRegistrationEnd(logDateFormatter.parse(split[4]));
			reg.setRegistrationStart(logDateFormatter.parse(split[5]));
			reg.setRegistrationSuccess(logDateFormatter.parse(split[6]));
			reg.setUserAgent(split[7]);
			reg.setUsername(split[8]);
			registrations.add(reg);
		}
		
		br.close();
		return registrations;
	}
	
	/**
	 * Inits the list.
	 *
	 * @return the list
	 */
	private static List<LoggedRegistration> initList() {
		try {
			return getAllRegistrations(new File("Logged Registrations"));
		} catch (IOException | ParseException  e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Gets the all registrations.
	 *
	 * @param folder the folder
	 * @return the all registrations
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	private static List<LoggedRegistration> getAllRegistrations(File folder) throws IOException, ParseException{
		List<LoggedRegistration> registrations = new LinkedList<>();
		for (File file : folder.listFiles()) {
			if(file.isFile() && file.getName().endsWith(".tsv")){
				registrations.addAll(getRegistrations(file));
			}
		}
		return registrations;
	}
	
	
}
