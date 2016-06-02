package de.luh.chkater.spammerdetection.manager;

import java.util.List;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.utility.ThreadUtility;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


// TODO: Auto-generated Javadoc
/**
 * The Class InstancesCreator.
 */
public class InstancesCreator {

	/** The user position. */
	private int userPosition;
	
	/** The features. */
	private List<AbstractFeature> features;
	
	/** The dataset name. */
	private String datasetName;

	/**
	 * Instantiates a new instances creator.
	 *
	 * @param features the features
	 * @param datasetName the dataset name
	 */
	public InstancesCreator(List<AbstractFeature> features, String datasetName) {
		super();
		this.features = features;
		this.datasetName = datasetName;
	}

	/**
	 * Creates the instances.
	 *
	 * @param users the users
	 * @return the instances
	 */
	public Instances createInstances(final List<User> users) {
		final Instances instances = new Instances(datasetName, createAttributeInfo(features), users.size());
		instances.setClassIndex(0);
		userPosition = -1;
		ThreadUtility.runTaskOnAllThreads(new Runnable() {

			@Override
			public void run() {
				buildInstance(users, instances);

			}
		});
		return instances;

	}

	/**
	 * Creates the attribute info.
	 *
	 * @param featureInstances the feature instances
	 * @return the fast vector
	 */
	private static FastVector createAttributeInfo(List<AbstractFeature> featureInstances) {
		FastVector attrInfo = new FastVector();
		for (int i = 0; i < featureInstances.size(); i++) {
			AbstractFeature feature = featureInstances.get(i);
			attrInfo.addElement(feature.getAttribute());

		}
		return attrInfo;
	}

	/**
	 * Adds the instance.
	 *
	 * @param instances the instances
	 * @param instance the instance
	 */
	private static synchronized void addInstance(Instances instances, Instance instance) {
		instances.add(instance);
	}

	/**
	 * Gets the user position.
	 *
	 * @return the user position
	 */
	private synchronized int getUserPosition() {
		this.userPosition++;
		return this.userPosition;
	}

	/**
	 * Builds the instance.
	 *
	 * @param users the users
	 * @param instances the instances
	 */
	private void buildInstance(List<User> users, Instances instances) {
		while (true) {
			int pos = getUserPosition();
			if (pos >= users.size()) {
				break;
			}
			User user = users.get(pos);
			Instance instance = createInstance(user);
			addInstance(instances, instance);

		}
	}

	/**
	 * Creates the instance.
	 *
	 * @param user the user
	 * @return the instance
	 */
	public Instance createInstance(User user) {
		Instance instance = new DenseInstance(features.size());
		for (AbstractFeature feature : features) {
			feature.apply(user, instance);
		}
		return instance;
	}
}
