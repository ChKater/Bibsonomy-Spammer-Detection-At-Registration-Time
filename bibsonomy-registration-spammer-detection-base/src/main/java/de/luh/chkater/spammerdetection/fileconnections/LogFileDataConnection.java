package de.luh.chkater.spammerdetection.fileconnections;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.log.LoggedRegistration;
import de.luh.chkater.spammerdetection.utility.UserImport;

/**
 * Reads user data from an file and join it with the results of the apache log
 *
 * @author kater
 */
public class LogFileDataConnection extends FileDataConnection{

	/**
	 * @param userfile
	 * @throws IOException
	 */
	public LogFileDataConnection(File userfile) throws IOException {
		super(filter(userfile));
	}

	/**
	 * @param userfile
	 * @return
	 * @throws IOException 
	 */
	private static List<User> filter(File userfile) throws IOException {
		List<User> users = UserImport.read(userfile);
		List<User> filtered = new ArrayList<>(users.size());
		for (User user : users) {
			LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
			if (loggedRegistration != null) {
				filtered.add(user);
			}
		}
		return filtered;
	}

}
