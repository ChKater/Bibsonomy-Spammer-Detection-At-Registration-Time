package de.luh.chkater.registrationspammerdetection.rest.dataconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.database.DBLogic;
import org.bibsonomy.model.User;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import de.luh.chkater.spammerdetection.manager.IDataConnection;

/**
 * Connection to the Bibsonomy Mysql server to recieve a single or all user. 
 *
 * @author Christian Kater
 */
public class DatabaseConnection implements IDataConnection {

	private static DatabaseConnection instance;

	public static DatabaseConnection get() {
		if (instance == null) {
			instance = new DatabaseConnection();
		}
		return instance;
	}

	private SqlMapClient smc;
	private Connection connection;

	private DatabaseConnection() {
		try {
			Class.forName(System.getProperty("database.main.driverClassName"));
			connection = DriverManager.getConnection(System.getProperty("database.main.url"),
					System.getProperty("database.main.username"), System.getProperty("database.main.password"));
			smc = SqlMapClientBuilder.buildSqlMapClient(DBLogic.class.getResourceAsStream("/SqlMapConfig.xml"));
			smc.setUserConnection(connection);
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public User getUser(String username) throws SQLException {
		smc.setUserConnection(connection);
		return (User) smc.queryForObject("getUserDetails", username);
	}

	@Override
	public List<User> getUsers() throws SQLException {
		smc.setUserConnection(connection);
		return smc.queryForList("getUsersForClassification");
	}
	
	@Override
	public void updateSpammerStatus(User user) throws SQLException {
		smc.setUserConnection(connection);
		smc.update("updateUser", user);		
	}

}
