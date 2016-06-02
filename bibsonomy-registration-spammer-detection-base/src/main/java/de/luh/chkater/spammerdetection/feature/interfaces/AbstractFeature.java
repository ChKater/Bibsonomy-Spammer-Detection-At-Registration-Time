package de.luh.chkater.spammerdetection.feature.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.User;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * Basic class for a feature
 *
 * @author Christian Kater
 */
public abstract class AbstractFeature {

	private Attribute attribute;
	protected final FeatureCategory category;
	protected final String name;
	protected final boolean needAllUser;
	protected final int version;

	/**
	 * @param name name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param needAllUser do the feature needs all users for precalculation
	 * @param attribute Weka-Wrapper for the feature
	 */
	public AbstractFeature(String name, int version, FeatureCategory category, boolean needAllUser, Attribute attribute) {
		super();
		this.name = name;
		this.version = version;
		this.category = category;
		this.needAllUser = needAllUser;
		this.setAttribute(attribute);		
	}

	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return this.attribute;
	}

	/**
	 * @return the category
	 */
	public FeatureCategory getCategory() {
		return this.category;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * @return the needAllUser
	 */
	public boolean isNeedAllUser() {
		return this.needAllUser;
	}

	//Feature which need all Features for precalculations have to override this Method.
	public void setUsers(List<User> user){}
	
	public abstract void apply(User user, Instance instance);

	/**
	 * @return a new instance
	 */
	public abstract AbstractFeature newInstance();

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
}
