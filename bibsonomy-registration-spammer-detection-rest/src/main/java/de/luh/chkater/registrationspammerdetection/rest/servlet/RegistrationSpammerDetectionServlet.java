package de.luh.chkater.registrationspammerdetection.rest.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bibsonomy.model.User;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.luh.chkater.registrationspammerdetection.rest.dataconnection.DatabaseConnection;
import de.luh.chkater.registrationspammerdetection.rest.manager.FeatureHelper;
import de.luh.chkater.registrationspammerdetection.rest.manager.RestSpammerDetectionManager;
import de.luh.chkater.spammerdetection.feature.factory.FeatureFactory;
import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.manager.IDataConnection;
import weka.attributeSelection.ASEvaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;

// TODO: Auto-generated Javadoc
/**
 * Servlet for REST server
 *
 * @author Christian Kater
 */
@Path("registrationspammerdetection")
public class RegistrationSpammerDetectionServlet {

	/** The Constant CONNECTION. */
	private static final IDataConnection CONNECTION = DatabaseConnection.get();

	/** The manager. */
	private static RestSpammerDetectionManager manager = new RestSpammerDetectionManager(CONNECTION,
			getDoubleProperty("classification.propability.no_spammer"),
			getDoubleProperty("classification.propability.no_spammer_not_sure"),
			getDoubleProperty("classification.propability.spammer_not_sure"));

	/** The gson. */
	private static Gson gson = new Gson();

	/**
	 * Gets the double property.
	 *
	 * @param name the name
	 * @return the double property
	 */
	private static double getDoubleProperty(String name) {
		return Double.parseDouble(System.getProperty(name));
	}
	
	/**
	 * Rebuild classifier.
	 *
	 * @throws Exception the exception
	 */
	public static void rebuildClassifier() throws Exception {
		FeatureHelper featureHelper = new FeatureHelper(CONNECTION);
		String featureNames = System.getProperty("feature.names");
		List<String> featureList = Arrays.asList(featureNames.split(","));
		if (featureNames.equals("all")) {
			featureList = FeatureFactory.getInstance().getFeatureNames();
		} else {
			featureList = Arrays.asList(featureNames.split(","));
		}
		boolean featureselection = System.getProperty("featureselection.activate").equals("true");

		List<AbstractFeature> features = FeatureFactory.getInstance().getFeatures(featureList);

		if (featureselection) {
			int numberOfFeatures = Integer.parseInt(System.getProperty("featureselection.number"));
			featureList = featureHelper.selectFeatures(CONNECTION.getUsers(), features, ASEvaluation.forName(System.getProperty("featureselection.metric.name"), split(System.getProperty("featureselection.metric.options"))), numberOfFeatures);
			features = FeatureFactory.getInstance().getFeatures(featureList);
		}
		featureHelper.initFeatures(features);
		Classifier classifier = null;
		
		boolean costsensitive = System.getProperty("classifier.costsensitive.activate").equals("true");
		if(costsensitive){
			CostSensitiveClassifier costClassifier = new CostSensitiveClassifier();
			CostMatrix cm = new CostMatrix(2);
			cm.setCell(0, 0, Double.parseDouble(System.getProperty("classifier.costsensitive.tn")));
			cm.setCell(0, 1, Double.parseDouble(System.getProperty("classifier.costsensitive.fp")));
			cm.setCell(1, 0, Double.parseDouble(System.getProperty("classifier.costsensitive.fn")));
			cm.setCell(1, 1, Double.parseDouble(System.getProperty("classifier.costsensitive.tp")));
			costClassifier.setCostMatrix(cm);
			costClassifier.setClassifier(AbstractClassifier.forName(System.getProperty("classifier.name"), split(System.getProperty("classifier.options"))));
			classifier = costClassifier;
		} else {
			classifier = AbstractClassifier.forName(System.getProperty("classifier.name"), split(System.getProperty("classifier.options")));
		}
		manager.set(classifier, features,
				System.getProperty("feature.dataset.name"));

	}

	/**
	 * Splits an weka option by space
	 *
	 * @param property the property
	 * @return the string[]
	 */
	private static String[] split(String property) {

		List<String> result = new ArrayList<>();
		boolean quoted = false;
		String curStr = "";
		for (int i = 0; i < property.length(); i++) {
			char cur = property.charAt(i);
			if (!quoted && cur == ' ') {
				result.add(curStr);
				curStr = "";
			} else {
				if (cur == '"') {
					quoted = !quoted;
				}
				curStr += cur;
			}
		}
		if (curStr.length() > 0) {
			result.add(curStr);
		}
		return (String[]) result.toArray(new String[] {});
	}

	/**
	 * Classify.
	 *
	 * @param userStr the user as json string
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("classify")
	public String classify(@FormParam("user") String userStr) throws Exception {
		User user = gson.fromJson(userStr, User.class);
		Integer classify = manager.classify(user);

		JsonObject result = new JsonObject();
		result.addProperty("algorithm", System.getProperty("classifier.name"));
		result.addProperty("spamStatus", classify);
		return result.toString();
	}

	/**
	 * Rebuild.
	 *
	 * @return success if the rebuild succeeded
	 * @throws Exception the exception
	 */
	@GET
	@Path("rebuild")
	@Produces(MediaType.TEXT_PLAIN)
	public String rebuild() throws Exception {
		rebuildClassifier();
		return "success";
	}

}
