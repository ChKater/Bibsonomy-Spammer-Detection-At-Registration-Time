package de.luh.chkater.spammerdetection.feature.impl;

import java.util.List;
import java.util.function.Function;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.FeatureCategory;
import de.luh.chkater.spammerdetection.feature.interfaces.NumericFeature;
import de.luh.chkater.spammerdetection.transformation.CountOnPopulation;

/**
 * Counts the occourence of values of an specific property of an user
 *
 * @author Christian Kater
 */
public class CountFeature extends NumericFeature implements Function<User, Double>{

	private CountOnPopulation count;
	private Function<User, String> whatToCount;
	private Function<User, Boolean> ignore;

	/**
 * @param name name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param transformation transform 
	 * @param whatToCount transform the user in the value to count
	 * @param ignore which user should be ignored
	 */
	public CountFeature(String name, int version, FeatureCategory category, Function<User, String> whatToCount, Function<User, Boolean> ignore) {
		super(name, version, category, true, null);
		super.transformation = this;
		this.count = new CountOnPopulation(whatToCount, ignore);
		this.whatToCount = whatToCount;
		this.ignore = ignore;
	}

	@Override
	public Double apply(User user) {
		return count.apply(user);
	}
	
	@Override
	public void setUsers(List<User> user) {
		count.setUsers(user);
	}
	
	@Override
	public AbstractFeature newInstance() {
		return new CountFeature(getName(), getVersion(), getCategory(), whatToCount, ignore);
	}

}
