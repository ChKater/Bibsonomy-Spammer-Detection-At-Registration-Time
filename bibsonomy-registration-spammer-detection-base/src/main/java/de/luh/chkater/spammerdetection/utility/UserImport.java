package de.luh.chkater.spammerdetection.utility;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.User;

/**
 * 
 * Reads the user data from an tsv file.
 *
 * @author Christian Kater
 */
public class UserImport {
	private static SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final int NUMBER_OF_COLUMS = 12;
	
	static int nSAd = 0;
	static int nS = 0;
	
	static int nAd = 0;
	static int n = 0;
	
	static int total = 0;
	static int totalAd = 0;
	
	static int totalS = 0;
	static int totalSAd = 0;
	
	public static List<User> read(File input) throws IOException{
		return read(input, 12);
	}
	
	public static List<User> read(File input, int numberOfLine) throws IOException{
		List<User> users = new LinkedList<>();

		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = br.readLine();
		
		while((line = br.readLine())!= null){
			if(line.startsWith("\n")){
//				System.out.println("Corrupted start of line");
				continue;
			}
			String[] split = line.split("\t");
			if(split.length != numberOfLine){
//				System.out.println("Not enough Data in row");
//				System.out.println(line);
//				System.out.println("---");
				continue;
			}
			try {
				User user = new User();
				user.setName(normalize(split[0]));
				user.setEmail(normalize(split[1]));
				try {
					user.setHomepage(new URL(normalize(split[2])));
				} catch (Exception e) {
					
				}
				user.setRealname(normalize(split[3]));
				try {
					user.setSpammer(Integer.parseInt(split[4]) == 1);
				} catch (NumberFormatException e1) {
					System.out.println(line);
				}
				try {
					user.setRegistrationDate(dateformatter.parse(normalize(split[5])));
				} catch (Exception e) {
					
				}
				user.setIPAddress(normalize(split[6]));
				user.setUpdatedBy(normalize(split[9]));
				try {
					user.setUpdatedAt(dateformatter.parse(normalize(split[10])));
				} catch (Exception e) {
					
				}
				
				if(split.length > 12){
					user.setRegistrationLog(normalize(split[12]));
				}
				
				if(Character.isDigit(user.getName().charAt(0)) && !user.getUpdatedBy().equals("classifier") && user.getRegistrationDate() != null){
					if(user.isSpammer()){
						nSAd++;
					}else {
						nAd++;
					}
					
				}
				if(Character.isDigit(user.getName().charAt(0)) ){
					if(user.isSpammer()){
						nS++;
					}else {
						n++;
					}
				}
				if(!user.getUpdatedBy().equals("classifier") && user.getRegistrationDate() != null){
					if(user.isSpammer()){
						totalSAd++;
					}else {
						totalAd++;
					}
				}
				if(user.isSpammer()){
					totalS++;
				}else {
					total++;
				}
				
				users.add(user);
			} catch (NumberFormatException e) {
				e.printStackTrace();
//				System.out.println("error with: " + line);
				continue;
			}
			
			

		}
//		System.out.println("digit all: Nicht-Spammer (" + n + " / " + total + ") Spammer(" + nS + " / " + totalS + ")");
//		System.out.println("digit dataset: Nicht-Spammer (" + nAd + " / " + totalAd + ") Spammer(" + nSAd + " / " + totalSAd + ")");
		br.close();
		return users;
	}

	/**
	 * @param string
	 * @return
	 */
	private static String normalize(String string) {
		if(string.startsWith("\"") && string.endsWith("\"")){
			string = string.substring(1, string.length() - 1);
		}
		if(string == null || string.length() == 0 || string.equals("NULL")){
			return null;
		}
		return string;
	}
	

}
