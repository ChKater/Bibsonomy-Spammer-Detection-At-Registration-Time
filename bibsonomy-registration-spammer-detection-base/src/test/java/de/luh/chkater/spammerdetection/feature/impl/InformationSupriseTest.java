package de.luh.chkater.spammerdetection.feature.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.bibsonomy.model.User;
import org.junit.Test;

import de.luh.chkater.spammerdetection.feature.interfaces.FeatureCategory;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class InformationSupriseTest {

	@Test
	public void test() {
		InformationSuprise is = new InformationSuprise("test", 1, FeatureCategory.LANGUAGE, new Function<User, String>() {
			
			@Override
			public String apply(User user) {
				return user.getName();
			}
		});
		
		List<User> users = new ArrayList<>(3);
		users.add(generateUser("kater"));
		users.add(generateUser("admin"));
		users.add(generateUser("user"));
		
		is.setUsers(users);
		
		assertEquals(0.4419, is.apply(generateUser("kater")), 0.01);
		assertEquals(0.4419, is.apply(generateUser("admin")), 0.01);
		assertEquals(0.7514, is.apply(generateUser("user")), 0.01);
		
		
	}

	private User generateUser(String username){
		User user = new User();
		user.setName(username);
		return user;
	}
}
