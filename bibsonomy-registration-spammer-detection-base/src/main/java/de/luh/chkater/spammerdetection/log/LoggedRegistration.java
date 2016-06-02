package de.luh.chkater.spammerdetection.log;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;



/**
 * Model class for logged registration from the apache log.
 */
public class LoggedRegistration {
	
	/** The registration map. */
	private static Map<String, LoggedRegistration> registrationMap = initMap();
	
	/**
	 * Inits the map.
	 *
	 * @return the map
	 */
	private static Map<String, LoggedRegistration> initMap() {
		HashMap<String, LoggedRegistration> map = new HashMap<>();
		for (LoggedRegistration loggedRegistration : LoggedRegistrationReader.getLoggedRegistrations()) {
			map.put(loggedRegistration.getUsername(), loggedRegistration);
		}
		return map;
	}
	
	/**
	 * Gets the.
	 *
	 * @param username the username
	 * @return the logged registration
	 */
	public static LoggedRegistration get(String username){
		return registrationMap.get(username);
	}


	/** The username. */
	private String username;
	
	/** The registration start. */
	private Date registrationStart;
	
	/** The registration end. */
	private Date registrationEnd;
	
	/** The registration success. */
	private Date registrationSuccess;
	
	/** The activation. */
	private Date activation;
	
	/** The number of errors. */
	private int numberOfErrors;
	
	/** The referer. */
	private String referer;
	
	/** The user agent. */
	private String userAgent;
	
	/** The ip. */
	private String ip;

	
	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the registration start.
	 *
	 * @return the registration start
	 */
	public Date getRegistrationStart() {
		return registrationStart;
	}

	/**
	 * Sets the registration start.
	 *
	 * @param registrationStart the new registration start
	 */
	public void setRegistrationStart(Date registrationStart) {
		this.registrationStart = registrationStart;
	}

	/**
	 * Gets the registration success.
	 *
	 * @return the registration success
	 */
	public Date getRegistrationSuccess() {
		return registrationSuccess;
	}

	/**
	 * Sets the registration success.
	 *
	 * @param registrationSuccess the new registration success
	 */
	public void setRegistrationSuccess(Date registrationSuccess) {
		this.registrationSuccess = registrationSuccess;
	}

	/**
	 * Gets the activation.
	 *
	 * @return the activation
	 */
	public Date getActivation() {
		return activation;
	}

	/**
	 * Sets the activation.
	 *
	 * @param activation the new activation
	 */
	public void setActivation(Date activation) {
		this.activation = activation;
	}

	/**
	 * Gets the number of errors.
	 *
	 * @return the number of errors
	 */
	public int getNumberOfErrors() {
		return numberOfErrors;
	}

	/**
	 * Sets the number of errors.
	 *
	 * @param numberOfErrors the new number of errors
	 */
	public void setNumberOfErrors(int numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	/**
	 * Gets the referer.
	 *
	 * @return the referer
	 */
	public String getReferer() {
		return referer;
	}

	/**
	 * Sets the referer.
	 *
	 * @param referer the new referer
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 * Gets the user agent.
	 *
	 * @return the user agent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Sets the user agent.
	 *
	 * @param userAgent the new user agent
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * Gets the ip.
	 *
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Sets the ip.
	 *
	 * @param ip the new ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Gets the registration end.
	 *
	 * @return the registration end
	 */
	public Date getRegistrationEnd() {
		return registrationEnd;
	}

	/**
	 * Sets the registration end.
	 *
	 * @param registrationEnd the new registration end
	 */
	public void setRegistrationEnd(Date registrationEnd) {
		this.registrationEnd = registrationEnd;
	}

}
