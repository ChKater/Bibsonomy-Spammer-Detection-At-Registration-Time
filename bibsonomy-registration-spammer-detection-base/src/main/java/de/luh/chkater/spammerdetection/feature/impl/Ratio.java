package de.luh.chkater.spammerdetection.feature.impl;

import java.util.List;
import java.util.function.Function;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.FeatureCategory;
import de.luh.chkater.spammerdetection.feature.interfaces.NumericFeature;
import de.luh.chkater.spammerdetection.transformation.CountOnPopulation;

/**
 * The ratio is the propability that a certain group of users have an value in
 * common relate to the propability that the value is in comon with someone of
 * the inverse group. 
 *
 * @author Christian Kater
 */
public class Ratio extends NumericFeature implements Function<User, Double> {

	private CountOnPopulation count;
	private CountOnPopulation antiCount;
	private Function<User, String> whatToCount;
	private Function<User, Boolean> normal;

	/**
	 * @param name Name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param transformation
	 * @param whatToCount defines which property of the user should to measure
	 * @param normal define the member of the group in which to look for common values
	 */
	public Ratio(String name, int version, FeatureCategory category, Function<User, String> whatToCount,
			final Function<User, Boolean> normal) {
		super(name, version, category, true, null);
		super.transformation = this;
		this.antiCount = new CountOnPopulation(whatToCount, normal);
		this.count = new CountOnPopulation(whatToCount, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				Boolean nUser = normal.apply(user);
				if (nUser == null) {
					return null;
				}
				return !nUser;
			}
		});
		this.whatToCount = whatToCount;
		this.normal = normal;
	}

	@Override
	public Double apply(User user) {
		Double nCount = count.apply(user);
		Double aCount = antiCount.apply(user);
		if (nCount == null || aCount == null) {
			return null;
		}
		if (aCount == 0) {
			return null;
		}
		return nCount.doubleValue() / aCount.doubleValue();
	}

	@Override
	public void setUsers(List<User> user) {
		count.setUsers(user);
		antiCount.setUsers(user);
	}

	
	@Override
	public AbstractFeature newInstance() {
		return new Ratio(getName(), getVersion(), getCategory(), whatToCount, normal);
	}
}
