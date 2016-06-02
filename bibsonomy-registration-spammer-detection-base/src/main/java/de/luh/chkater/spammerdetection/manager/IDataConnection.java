package de.luh.chkater.spammerdetection.manager;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.model.User;


// TODO: Auto-generated Javadoc
/**
 * The Interface IDataConnection.
 */
public interface IDataConnection {
	
	/**
	 * Gets the user.
	 *
	 * @param username the username
	 * @return the user
	 * @throws SQLException the SQL exception
	 */
	public User getUser(String username) throws SQLException;
	
	/**
	 * Gets the users.
	 *
	 * @return the users
	 * @throws SQLException the SQL exception
	 */
	public List<User> getUsers() throws SQLException;

	/**
	 * Update spammer status.
	 *
	 * @param user the user
	 * @throws SQLException the SQL exception
	 */
	public void updateSpammerStatus(User user) throws SQLException;
}
