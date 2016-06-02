package de.luh.chkater.spammerdetection.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.luh.chkater.spammerdetection.feature.factory.FeatureFactory;
import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.FeatureCategory;
import de.luh.chkater.spammerdetection.feature.interfaces.NumericFeature;
import de.luh.chkater.spammerdetection.fileconnections.FileDataConnection;
import de.luh.chkater.spammerdetection.fileconnections.LogFileDataConnection;
import de.luh.chkater.spammerdetection.manager.EvaluationSpammerDetectionManager;
import de.luh.chkater.spammerdetection.manager.IDataConnection;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class DistributionCreator {

	private static final String SEPERATOR = ";";

	public static void main(String[] args) throws Exception {
		DistributionCreator cr = new DistributionCreator();
		cr.user = new File(args[0]);
		cr.userRL = new File(args[1]);
		cr.out = new File(args[2]);
		cr.run();
	}
	
	private Map<String, Double> topIg = new HashMap<>();
	private Map<String, Double> topchi = new HashMap<>();
	

	private Instances normal;
	private List<AbstractFeature> normalFeature;

	private Instances log;
	private List<AbstractFeature> logFeature;

	private Instances interaction;
	private List<AbstractFeature> interactionFeature;

	private File out;
	private File user;
	private File userRL;

	public void run() throws Exception {
		normalFeature = getFeatures(FeatureCategory.LANGUAGE, FeatureCategory.ENVIRONMENT,
				FeatureCategory.POPULATION_BASED, FeatureCategory.KEYBOARD);
		normal = getInstances(normalFeature, new FileDataConnection(user));
		logFeature = getFeatures(FeatureCategory.LOG);
		log = getInstances(logFeature, new LogFileDataConnection(user));
		interactionFeature = getFeatures(FeatureCategory.INTERACTION);
		interaction = getInstances(interactionFeature, new FileDataConnection(userRL, 13));


		
		createDistributions(normalFeature, normal);
		createDistributions(logFeature, log);
		createDistributions(interactionFeature, interaction);
		System.out.println("Feature distributions created");
		save(normalFeature, normal);
		save(logFeature, log);
		save(interactionFeature, interaction);
		List<String> igKeys = new ArrayList<>(topIg.keySet());
		Collections.sort(igKeys, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				int compare = Double.compare(topIg.get(o1), topIg.get(o2)) * -1;
				if(compare == 0){
					return o1.compareTo(o2);
				}
				return compare;
			}
		});
		
		BufferedWriter brIG = new BufferedWriter(new FileWriter(new File(out , "IG_Top_25.tsv")));
		brIG.write("name;ig");
		for (int i = 0; i < 25; i++) {
			String key = igKeys.get(i);
			System.out.println(key+ SEPERATOR + topIg.get(key));
			brIG.write(key+ SEPERATOR + String.format("%.6f", topIg.get(key))  + System.lineSeparator());
		}
		brIG.close();
		
		List<String> chiKeys = new ArrayList<>(topIg.keySet());
		Collections.sort(chiKeys, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				int compare = Double.compare(topchi.get(o1), topchi.get(o2)) * -1;
				if(compare == 0){
					return o1.compareTo(o2);
				}
				return compare;
			}
		});
		
		BufferedWriter brChi = new BufferedWriter(new FileWriter(new File(out , "chi_Top_25.tsv")));
		brChi.write("name;chi");
		for (int i = 0; i < 25; i++) {
			String key = chiKeys.get(i);
			brChi.write(key+ SEPERATOR + topchi.get(key) + System.lineSeparator());
		}
		brChi.close();
	}

	private Instances getInstances(List<AbstractFeature> features, IDataConnection connection) {
		EvaluationSpammerDetectionManager manager = new EvaluationSpammerDetectionManager(connection, features);
		return manager.getInstances();
	}

	private List<AbstractFeature> getFeatures(FeatureCategory... categories) {
		return FeatureFactory.getInstance()
				.getFeatures(FeatureFactory.getInstance().getFeaturesFromCategoryList(Arrays.asList(categories)));
	}

	private void save(List<AbstractFeature> features, Instances instances) throws Exception {
		Collections.sort(features, new Comparator<AbstractFeature>() {

			@Override
			public int compare(AbstractFeature o1, AbstractFeature o2) {
				int compare = o1.getCategory().getName().compareTo(o2.getCategory().getName());
				if (compare == 0) {
					compare = o1.getName().compareTo(o2.getName());
				}
				return compare;
			}
		});

		InfoGainAttributeEval ig = new InfoGainAttributeEval();
		ig.buildEvaluator(instances);

		ChiSquaredAttributeEval chi = new ChiSquaredAttributeEval();
		chi.buildEvaluator(instances);
		
		

		String category = features.get(0).getCategory().getName();
		int page = 1;

		BufferedWriter br = new BufferedWriter(new FileWriter(new File(out, category + "_" + page + ".tsv")));
		String header = "name" + SEPERATOR + "ig" + SEPERATOR + "chisqu" + System.lineSeparator();
		br.write(header);
		int c = 0;
		for (AbstractFeature feature : features) {
		
			if (!category.equals(feature.getCategory().getName())) {
				br.close();
				page = 1;
				category = feature.getCategory().getName();
				br = new BufferedWriter(new FileWriter(new File(out, category + "_" + page + ".tsv")));
				br.write(header);
				c = 0;
			}
			if (c == 50) {
				br.close();
				page++;
				br = new BufferedWriter(new FileWriter(new File(out, category + "_" + page + ".tsv")));
				br.write(header);
				c = 0;
			}
			double currentIG = ig.evaluateAttribute(feature.getAttribute().index());
			topIg.put(feature.getName(), currentIG);
			double currentChi = chi.evaluateAttribute(feature.getAttribute().index());
			topchi.put(feature.getName(), currentChi);
			br.write(feature.getName() + SEPERATOR
					+ String.format("%.6f", currentIG) + SEPERATOR
					+ String.format("%.2f", currentChi) 
					+ System.lineSeparator());
			c++;
		}
		br.close();
		
		
		
	}

	private void createDistributions(List<AbstractFeature> features, Instances instances) throws IOException {
		for (AbstractFeature feature : features) {
			try {
				if (feature instanceof NumericFeature) {
					DistributionFactory.saveCCDF(instances, feature, out);
					DistributionFactory.saveIntegerDistribution(instances, feature, out);
				} else {
					DistributionFactory.saveNominalDistribution(instances, feature, out);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(feature.getName());
				System.exit(1);
			}
		}
	}
}
