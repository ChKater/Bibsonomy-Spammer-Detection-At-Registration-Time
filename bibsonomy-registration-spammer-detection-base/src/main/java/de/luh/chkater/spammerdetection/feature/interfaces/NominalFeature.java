package de.luh.chkater.spammerdetection.feature.interfaces;

import java.util.List;
import java.util.function.Function;

import org.bibsonomy.model.User;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

/**
 * Basic class for nominal feature
 *
 * @author kater
 */
public class NominalFeature extends AbstractFeature {

	private static Attribute createAttribute(String name, List<String> possibleValues) {
		FastVector vals = new FastVector(2);
		for (String value : possibleValues) {
			vals.addElement(value);
		}
		return  new Attribute(name, vals);
		
	}

	private final List<String> possibleValues;
	protected Function<User, String> transformation;

	/**
	 * @param name name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param needAllUser do the feature needs all users for precalculation
	 * @param possibleValues possible values
	 * @param transformation transform a user into one of the possible values
	 */
	public NominalFeature(String name, int version, FeatureCategory category, boolean needAllUser,
			List<String> possibleValues, Function<User, String> transformation) {
		super(name, version, category, needAllUser, createAttribute(name, possibleValues));
		this.possibleValues = possibleValues;
		this.transformation = transformation;
	}
	
	


	/**
	 * @return the possibleValues
	 */
	public List<String> getPossibleValues() {
		return this.possibleValues;
	}

	@Override
	public void apply(User user, Instance instance) {
		String value = transformation.apply(user);
		if (value == null || !possibleValues.contains(value)) {
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
		return new NominalFeature(getName(), getVersion(), getCategory(), isNeedAllUser(), getPossibleValues(), transformation);
	}




	
}
