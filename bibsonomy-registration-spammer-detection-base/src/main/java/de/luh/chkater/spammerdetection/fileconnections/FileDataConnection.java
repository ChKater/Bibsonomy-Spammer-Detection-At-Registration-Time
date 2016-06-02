package de.luh.chkater.spammerdetection.fileconnections;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.manager.IDataConnection;
import de.luh.chkater.spammerdetection.utility.UserImport;


/**
 * Reads user data from an tsv file
 *
 * @author Christian Kater
 */
public class FileDataConnection implements IDataConnection {

	private Map<String, User> users;

	protected FileDataConnection(List<User> userList) {
		users = new HashMap<>(userList.size());
		int i = 0;
		for (User user : userList) {
//			if(user.isSpammer() && i < 100){
//				continue;
//			}
			if (user.getRegistrationDate() != null && user.getRegistrationDate().getYear() > 0
					&& !user.getUpdatedBy().equals("classifier")) {
				users.put(user.getName(), user);
//				i++;
//				if(i == 100){
//					break;
//				}
			}
			
		}
	}

	public FileDataConnection(File userfile) throws IOException {
		this(UserImport.read(userfile));
		System.out.println("FileDataConnection created with " + users.keySet().size() + " user.");


	}
	
	public FileDataConnection(File userfile, int numberOfCol) throws IOException {
		this(UserImport.read(userfile, numberOfCol));
		System.out.println("FileDataConnection created with " + users.keySet().size() + " user.");


	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.luh.chkater.spammerdetection.manager.IDataConnection#getUser(java.lang
	 * .String)
	 */
	@Override
	public User getUser(String username) throws SQLException {
		return users.get(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.luh.chkater.spammerdetection.manager.IDataConnection#getUsers()
	 */
	@Override
	public List<User> getUsers() throws SQLException {
		return new ArrayList<>(users.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.luh.chkater.spammerdetection.manager.IDataConnection#
	 * updateSpammerStatus(org.bibsonomy.model.User)
	 */
	@Override
	public void updateSpammerStatus(User user) throws SQLException {
		throw new RuntimeException("Not supported!");

	}

}
