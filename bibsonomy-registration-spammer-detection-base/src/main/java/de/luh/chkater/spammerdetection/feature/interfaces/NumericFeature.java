package de.luh.chkater.spammerdetection.feature.interfaces;

import java.util.List;
import java.util.function.Function;

import org.bibsonomy.model.User;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * Basic class for numeric feature
 *
 * @author Christian Kater
 */
public class NumericFeature extends AbstractFeature {
	
	private static Attribute createAttribute(String name){
		return new Attribute(name);
	}

	protected Function<User, Double> transformation;

	/**
	 * @param name name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param needAllUser do the feature needs all users for precalculation
	 * @param transformation transform a user into an numeric value
	 */
	public NumericFeature(String name, int version, FeatureCategory category, boolean needAllUser,
			Function<User, Double> transformation) {
		super(name, version, category, needAllUser, createAttribute(name));
		this.transformation = transformation;
	}
	

	

	




	@Override
	public void apply(User user, Instance instance) {
		Double value = transformation.apply(user);
	
		if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {
			instance.setMissing(getAttribute());
		} else {
			try {
				instance.setValue(getAttribute(), value);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(instance.numAttributes());
				System.out.println(getAttribute().index());
				System.exit(1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.luh.chkater.spammerdetection.feature.interfaces.newF.AbstractFeature#newInstance()
	 */
	@Override
	public AbstractFeature newInstance() {
		return new NumericFeature(getName(), getVersion(), getCategory(), isNeedAllUser(), transformation);
	}
	
	

}
