package de.luh.chkater.spammerdetection.statistics;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.fileconnections.FileDataConnection;
import de.luh.chkater.spammerdetection.fileconnections.LogFileDataConnection;
import de.luh.chkater.spammerdetection.utility.UserImport;

/**
 * TODO: add documentation to this class
 *
 * @author Christian Kater
 */
public class Count {
	public static void main(String[] args) throws Exception {
		List<User> users = new FileDataConnection(new File(args[0]), 13).getUsers();
		
		int spammer = 0;
		int noSpammer = 0;
		int spammerHomepage = 0;
		int noSpammerHomepage = 0;
		int spammerRealname = 0;
		int noSpammerRealname = 0;
		Date spammerMin = null;
		Date spammerMax = null; 
		Date noSpammerMin = null;
		Date noSpammerMax = null;
		
		for (User user : users) {
			if(/*!user.getUpdatedBy().equals("classifier") &&*/ user.getRegistrationDate().getYear() > 0){
				if(user.isSpammer()){
					spammer++;
					if(user.getHomepage() != null){
						spammerHomepage++;
					}
					if(user.getRealname() != null && user.getRealname().length() >0){
						spammerRealname++;
					}
					if(spammerMin == null || (user.getRegistrationDate() != null && user.getRegistrationDate().before(spammerMin))){
						spammerMin = user.getRegistrationDate();
					}
					if(spammerMax == null || (user.getRegistrationDate() != null && user.getRegistrationDate().after(spammerMax))){
						spammerMax = user.getRegistrationDate();
					}
				} else {
					noSpammer++;
					if(user.getHomepage() != null){
						noSpammerHomepage++;
					}
					if(user.getRealname() != null && user.getRealname().length() >0){
						noSpammerRealname++;
					}
					if(noSpammerMin == null || (user.getRegistrationDate() != null && user.getRegistrationDate().before(noSpammerMin))){
						noSpammerMin = user.getRegistrationDate();
					}
					if(noSpammerMax == null || (user.getRegistrationDate() != null && user.getRegistrationDate().after(noSpammerMax))){
						noSpammerMax = user.getRegistrationDate();
					}
				}
			}
		}
		
		System.out.println("Spammer: " + spammer + " davon mit Homepage: " + spammerHomepage + "; mit Realname: " + spammerRealname + " early: " + spammerMin.toGMTString() + " late: " + spammerMax.toGMTString());
		System.out.println("Non-Spammer: " + noSpammer + " davon mit Homepage: " + noSpammerHomepage + "; mit Realname: " + noSpammerRealname + " early: " + noSpammerMin.toGMTString() + " late: " + noSpammerMax.toGMTString());
	}

}
