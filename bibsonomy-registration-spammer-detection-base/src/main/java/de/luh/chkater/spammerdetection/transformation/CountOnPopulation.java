package de.luh.chkater.spammerdetection.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.utility.ThreadUtility;

/**
 * 
 * Counts the occourence of an value
 *
 * @author Christian Kater
 */
public class CountOnPopulation implements Function<User, Double> {

	private Map<String, Integer> cache = new HashMap<>();
	private Function<User, String> whatToCount;
	private Function<User, Boolean> ignore;
	private int currentUser;
	private double total;

	

	/**
	 * @param whatToCount transform the user into a value, which will be count
	 * @param ignore which user should be ignored
	 */
	public CountOnPopulation(Function<User, String> whatToCount, Function<User, Boolean> ignore) {
		super();
		this.whatToCount = whatToCount;
		this.ignore = ignore;
	}

	@Override
	public Double apply(User user) {
		String word = whatToCount.apply(user);
		if (word == null || word.trim().length() == 0) {
			return null;
		}
		Integer count = cache.get(word);
		

		return count == null ? 0 : new Double(count.doubleValue() / total);
	}

	private synchronized void increaseCount(String key) {
		Integer count = cache.get(key);
		if (count == null) {
			cache.put(key, 0);
		} else {
			cache.put(key, count + 1);
		}
		total++;
	}

	private synchronized int getNextUser() {
		currentUser++;
		return currentUser;
	}

	private void calculateCount(List<User> users) {
		while (true) {
			int pos = getNextUser();
			if (pos >= users.size()) {
				break;
			}
			User user = users.get(pos);
			if(ignore.apply(user)){
				continue;
			}
			String word = whatToCount.apply(user);
			increaseCount(word);
		}
	}

	
	public void setUsers(List<User> user) {
		cache.clear();
		currentUser = -1;
		total = 0;
		final List<User> finalUsers = user;
		ThreadUtility.runTaskOnAllThreads(new Runnable() {

			@Override
			public void run() {
				calculateCount(finalUsers);

			}
		});
	}
	
}
