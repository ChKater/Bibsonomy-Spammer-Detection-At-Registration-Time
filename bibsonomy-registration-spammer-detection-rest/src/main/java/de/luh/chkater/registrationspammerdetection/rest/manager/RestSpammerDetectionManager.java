package de.luh.chkater.registrationspammerdetection.rest.manager;

import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.manager.IDataConnection;
import de.luh.chkater.spammerdetection.manager.InstancesCreator;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

// TODO: Auto-generated Javadoc
/**
 * Manager to classify user and rebuild the classifier.
 *
 * @author kater
 */
public class RestSpammerDetectionManager {

	/** The classifier. */
	private Classifier classifier;

	/** The current id. */
	private int currentId = 0;
	
	/** The data connection. */
	protected IDataConnection dataConnection;

	/** The features. */
	private List<AbstractFeature> features;
	
	/** The instance creator. */
	private InstancesCreator instanceCreator;

	/** The maintenance thread. */
	private Thread maintenanceThread = null;

	/** The Spammer not sure. */
	private double noSpammer, noSpammerNotSure, SpammerNotSure;

	/** The running classifications. */
	private Integer runningClassifications;

	/**
	 * Instantiates a new rest spammer detection manager.
	 *
	 * @param dataConnection the data connection
	 * @param noSpammer the max propability to count user as non spammer. 
	 * @param noSpammerNotSure max propability to count user as non spammer, but mark decision as unsure. 
	 * @param spammerNotSure max propability to count user as spammer, but mark decision as unsure. 
	 */
	public RestSpammerDetectionManager(IDataConnection dataConnection, double noSpammer, double noSpammerNotSure,
			double spammerNotSure) {
		super();
		this.dataConnection = dataConnection;
		this.noSpammer = noSpammer;
		this.noSpammerNotSure = noSpammerNotSure;
		SpammerNotSure = spammerNotSure;
		this.runningClassifications = 0;
	}

	/**
	 * Sets the new classifier and features
	 *
	 * @param newClassifier the new classifier
	 * @param newFeatures the new features
	 * @param datasetName the dataset name
	 * @throws Exception the exception
	 */
	public void set(final Classifier newClassifier, final List<AbstractFeature> newFeatures,
			final String datasetName) throws Exception {
		InstancesCreator instancesCreator = new InstancesCreator(newFeatures, datasetName);
		Instances instances = instancesCreator.createInstances(dataConnection.getUsers());
		newClassifier.buildClassifier(instances);
		maintenanceThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					boolean classificationIsRunning;
					synchronized (runningClassifications) {
						classificationIsRunning = runningClassifications > 0;
					}
					if (!classificationIsRunning) {
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// log.error("TODO", e);
					}
				}
				classifier = newClassifier;
				features = newFeatures;
				instanceCreator = new InstancesCreator(newFeatures, datasetName);
				maintenanceThread = null;
			}
		});
		maintenanceThread.start();
		if (maintenanceThread != null) {
			maintenanceThread.join();
		}
	}

	/**
	 * classify a user
	 *
	 * @param user the user
	 * @return the spammer status of the user
	 * @throws Exception the exception
	 */
	public Integer classify(User user) throws Exception {
		if (features == null || classifier == null || instanceCreator == null) {
			throw new RuntimeException("Not initialized");
		}
		Instance instance = instanceCreator.createInstance(user);
		while (maintenanceThread != null) {
			maintenanceThread.join();
		}
		signIn();
		double propability = classifier.distributionForInstance(instance)[0];
		signOut();
		int status;
		if (propability <= noSpammer) {
			status = SpamStatus.NO_SPAMMER.getId();
			user.setPrediction(0);
			user.setToClassify(0);
			user.setSpammer(false);
		} else if (propability <= noSpammerNotSure) {
			status =SpamStatus.NO_SPAMMER_NOT_SURE.getId();
			user.setPrediction(0);
			user.setToClassify(1);
			user.setSpammer(false);
		} else if (propability <= SpammerNotSure) {
			status =SpamStatus.SPAMMER_NOT_SURE.getId();
			user.setPrediction(1);
			user.setToClassify(1);
			user.setSpammer(true);
		} else {
			status =SpamStatus.SPAMMER.getId();
			user.setPrediction(1);
			user.setToClassify(0);
			user.setSpammer(true);
		}
		user.setUpdatedBy("registration_classifier");
		user.setUpdatedAt(new Date());
//		dataConnection.updateSpammerStatus(user);
		return status;
	}

	/**
	 * Sign in.
	 *
	 */
	private synchronized void signIn() {
		currentId++;
		if (currentId == Integer.MAX_VALUE) {
			currentId = 0;
		}
		synchronized (runningClassifications) {
			runningClassifications++;
		}
	}

	/**
	 * Sign out.
	 *
	 */
	private void signOut() {
		synchronized (runningClassifications) {
			runningClassifications--;
		}
	}
}
