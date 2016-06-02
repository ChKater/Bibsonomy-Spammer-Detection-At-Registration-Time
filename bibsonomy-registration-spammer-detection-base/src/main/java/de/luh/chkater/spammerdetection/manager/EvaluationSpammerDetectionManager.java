package de.luh.chkater.spammerdetection.manager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.utility.ThreadUtility;
import de.luh.chkater.weka.PopulationbaseEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


/**
 * The Class EvaluationSpammerDetectionManager.
 */
public class EvaluationSpammerDetectionManager {

	/** The Constant DATASETNAME. */
	private static final String DATASETNAME = "RegistrationSpammerDetection-Evaluation";
	
	/** The connection. */
	private IDataConnection connection;
	
	/** The feature pos. */
	private int featurePos;
	
	/** The features. */
	private List<AbstractFeature> features;
	
	/** The instances. */
	private Instances instances;
	
	/** The position to user. */
	private Map<Integer, String> positionToUser;
	
	/** The random. */
	private Random random = new Random();
	
	/** The user position. */
	private int userPosition;

	/**
	 * Instantiates a new evaluation spammer detection manager.
	 *
	 * @param connection the connection
	 * @param features the features
	 */
	public EvaluationSpammerDetectionManager(IDataConnection connection, List<AbstractFeature> features) {
		super();
		this.connection = connection;
		this.features = features;
		try {
			rebuildFeatureDataset();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Adds the instance.
	 *
	 * @param instance the instance
	 * @return the int
	 */
	private synchronized int addInstance(Instance instance) {
		instances.add(instance);
		return instances.numInstances() - 1;
	}

	/**
	 * Assign position to user.
	 *
	 * @param pos the pos
	 * @param username the username
	 */
	private synchronized void assignPositionToUser(int pos, String username) {
		positionToUser.put(pos, username);
	}

	/**
	 * Builds the instance.
	 *
	 * @param users the users
	 */
	private void buildInstance(List<User> users) {
		while (true) {
			int pos = getUserPosition();
			if (pos >= users.size()) {
				break;
			}

			Instance instance = new DenseInstance(features.size());
			User user = users.get(pos);
			for (AbstractFeature feature : features) {
				feature.apply(user, instance);
			}
			int posInInstances = addInstance(instance);
			assignPositionToUser(posInInstances, user.getName());

		}
	}

	/**
	 * Creates the attribute info.
	 *
	 * @param featureInstances the feature instances
	 * @return the fast vector
	 */
	private FastVector createAttributeInfo(List<AbstractFeature> featureInstances) {
		FastVector attrInfo = new FastVector(featureInstances.size());
		for (int i = 0; i < featureInstances.size(); i++) {
			AbstractFeature feature = featureInstances.get(i);
			attrInfo.addElement(feature.getAttribute());
		}
		return attrInfo;
	}

	/**
	 * Evaluate.
	 *
	 * @param classifier the classifier
	 * @return the evaluation
	 * @throws Exception the exception
	 */
	public Evaluation evaluate(Classifier classifier) throws Exception {
		PopulationbaseEvaluation evaluation = new PopulationbaseEvaluation(getInstances());
		evaluation.crossValidateModel(classifier, getInstances(), 10, random, positionToUser,
				new Function<String, User>() {

					@Override
					public User apply(String username) {
						try {
							return connection.getUser(username);
						} catch (SQLException e) {
							e.printStackTrace();
							System.exit(1);
						}
						return null;
					}
				}, features);
		return evaluation;
	}

	/**
	 * Gets the instances.
	 *
	 * @return the instances
	 */
	public Instances getInstances() {
		return instances;
	}

	/**
	 * Gets the next feature position.
	 *
	 * @return the next feature position
	 */
	private synchronized int getNextFeaturePosition() {
		featurePos++;
		return featurePos;
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
	 * Inits the feature.
	 *
	 * @param features the features
	 * @param users the users
	 */
	private void initFeature(List<AbstractFeature> features, List<User> users) {
		while (true) {
			int pos = getNextFeaturePosition();
			if (pos >= features.size()) {
				break;
			}
			AbstractFeature feature = features.get(pos);
			if (feature.isNeedAllUser()) {
				feature.setUsers(users);
			}
		}
	}

	/**
	 * Rebuild feature dataset.
	 *
	 * @throws Exception the exception
	 */
	public void rebuildFeatureDataset() throws Exception {
		final List<User> users = connection.getUsers();
		positionToUser = new HashMap<>();
		featurePos = -1;
		ThreadUtility.runTaskOnAllThreads(new Runnable() {

			@Override
			public void run() {
				initFeature(features, users);

			}
		});
		instances = new Instances(DATASETNAME, createAttributeInfo(features), users.size());
		instances.setClassIndex(0);
		userPosition = -1;
		ThreadUtility.runTaskOnAllThreads(new Runnable() {

			@Override
			public void run() {
				buildInstance(users);

			}
		});

	}

}
