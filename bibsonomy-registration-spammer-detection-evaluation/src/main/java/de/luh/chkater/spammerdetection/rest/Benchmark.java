package de.luh.chkater.spammerdetection.rest;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bibsonomy.model.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import de.luh.chkater.spammerdetection.fileconnections.FileDataConnection;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class Benchmark {
	
	private static Properties regSpamDetProps = getRegSpamProperties();
	
	private static List<User> users;

	private static Random rnd = new Random();
	
	private static Properties getRegSpamProperties() {
		try {
			Properties props = new Properties();
			props.load(Benchmark.class.getResourceAsStream("/registrationspammerdetection.properties"));
			return props;
		} catch (IOException e) {
			//log.error("could not load registrationspammerdetection.properties", e);
			return null;
		}
	}

	/**
	 * @return
	 * @throws IOException 
	 * @throws SQLException 
	 */
	private static List<User> getUsers(File in) throws IOException, SQLException {
		FileDataConnection con = new FileDataConnection(in);
		return con.getUsers();
	}

	private static void main(String[] args) throws HttpException, IOException, ParseException, SQLException {
		users = getUsers(new File(args[0]));
		int repeat = 100;
		double mean = 0;
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		for (int i = 0; i < repeat; i++) {
			long start = System.currentTimeMillis();
//			sendUser(getUser());
//			rebuild();
			long end = System.currentTimeMillis();
			long time = (end - start);
			if(time > max){
				max = time;
			}
			if(time < min){
				min = time;
			}
			mean += time;
		}
		System.out.println("Durchschn. Zeit: " + mean / repeat + ", min: " + min + ", max: " + max);
	}
	
	private static User getUser(){
		int pos = rnd.nextInt(users.size());
		return users.get(pos);
	}
	
	
	private static void sendUser(User pendingUser) throws HttpException, IOException, ParseException{
		Gson gson = new Gson();
		HttpClient client = new HttpClient();
		client.setTimeout(Integer.parseInt(regSpamDetProps.getProperty("registrationspammerdetection.timeout")));
		Credentials credentials = new UsernamePasswordCredentials(regSpamDetProps.getProperty("registrationspammerdetection.user"),
				regSpamDetProps.getProperty("registrationspammerdetection.password"));
		client.getState().setCredentials(AuthScope.ANY, credentials);
		
		PostMethod method = new PostMethod(regSpamDetProps.getProperty("registrationspammerdetection.url"));
		method.addParameter("user", gson.toJson(pendingUser));
		client.executeMethod(method);
		String response = method.getResponseBodyAsString();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(response);
		//if respone is an integer the classifaction was successfull
		int status = ((Long) jsonObject.get("spamStatus")).intValue();
	}
	
	
	private static void rebuild() throws HttpException, IOException{
		HttpClient client = new HttpClient();
		client.setTimeout(Integer.MAX_VALUE);
		Credentials credentials = new UsernamePasswordCredentials(regSpamDetProps.getProperty("registrationspammerdetection.user"),
				regSpamDetProps.getProperty("registrationspammerdetection.password"));
		client.getState().setCredentials(AuthScope.ANY, credentials);
		
		GetMethod method = new GetMethod(regSpamDetProps.getProperty("registrationspammerdetection.rebuild"));
		
		client.executeMethod(method);
		String response = method.getResponseBodyAsString();
	}
}
