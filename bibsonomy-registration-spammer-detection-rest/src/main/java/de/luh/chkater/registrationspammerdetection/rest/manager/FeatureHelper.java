package de.luh.chkater.registrationspammerdetection.rest.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.manager.IDataConnection;
import de.luh.chkater.spammerdetection.manager.InstancesCreator;
import de.luh.chkater.spammerdetection.utility.ThreadUtility;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;

// TODO: Auto-generated Javadoc
/**
 *Helper class for initialize and select features.
 *
 * @author kater
 */
public class FeatureHelper {

	/** The feature pos. */
	private int featurePos;

	/** The features. */
	private List<AbstractFeature> features;
	
	/** The data connection. */
	protected IDataConnection dataConnection;

	

	
	/**
	 * Instantiates a new feature helper.
	 *
	 * @param dataConnection the data connection
	 */
	public FeatureHelper(IDataConnection dataConnection) {
		super();
		this.dataConnection = dataConnection;
	}

	/**
	 * Select features.
	 *
	 * @param users the users
	 * @param features base features
	 * @param evaluator feature selection evaluation method
	 * @param numberOfFeatures the number of features to select
	 * @return the top features
	 * @throws Exception the exception
	 */
	public List<String> selectFeatures(final List<User> users, List<AbstractFeature> features,
			ASEvaluation evaluator, int numberOfFeatures) throws Exception {
		initFeatures(features);
		Ranker ranker = new Ranker();
		ranker.setThreshold(0);
		ranker.setNumToSelect(numberOfFeatures);
		
		AttributeSelection attsel = new AttributeSelection();
		attsel.setSearch(ranker);
		attsel.setEvaluator(evaluator);
		
		InstancesCreator instancesCreator = new InstancesCreator(features, "featureselection");
		Instances instances = instancesCreator.createInstances(users);
			
		attsel.SelectAttributes(instances);
		List<String> featurenames = new ArrayList<>();
		int[] selected = attsel.selectedAttributes();
		for (AbstractFeature feature : features) {
			for (int i = 0; i < selected.length; i++) {
				if(selected[i] == feature.getAttribute().index()){
					featurenames.add(feature.getName());
				}
			}
		}
		
		return featurenames;
	}
	
	/**
	 * Inits the features.
	 *
	 * @param features the features
	 * @throws SQLException the SQL exception
	 */
	public void initFeatures(List<AbstractFeature> features) throws SQLException{
		this.features = features;
		featurePos = -1;
		final List<User> users = dataConnection.getUsers();
		ThreadUtility.runTaskOnAllThreads(new Runnable() {

			@Override
			public void run() {
				initFeature(users);

			}
		});
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
	 * Inits the feature.
	 *
	 * @param users the users
	 */
	private void initFeature(List<User> users) {
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
}
