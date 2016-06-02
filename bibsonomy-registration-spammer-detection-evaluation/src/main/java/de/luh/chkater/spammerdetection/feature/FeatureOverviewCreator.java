package de.luh.chkater.spammerdetection.feature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.luh.chkater.spammerdetection.feature.factory.FeatureFactory;
import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.fileconnections.FileDataConnection;
import de.luh.chkater.spammerdetection.manager.EvaluationSpammerDetectionManager;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class FeatureOverviewCreator {

	private static final String SEPERATOR = ";";

	public static void main(String[] args) throws Exception {
		List<AbstractFeature> features = FeatureFactory.getInstance().getAllFeatures();
		EvaluationSpammerDetectionManager manager = new EvaluationSpammerDetectionManager(
				new FileDataConnection(new File(args[0])), features);

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
		saveArff(manager.getInstances());

		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		eval.setBinarizeNumericAttributes(true);
		eval.buildEvaluator(manager.getInstances());

		String category = features.get(0).getCategory().getName();
		int page = 1;

		BufferedWriter br = new BufferedWriter(
				new FileWriter(new File(args[1] + "_" + category + "_" + page + ".tsv")));
		br.write("name" + SEPERATOR + "informationsgewinn" + System.lineSeparator());
		int c = 0;
		for (AbstractFeature feature : features) {
			if (!category.equals(feature.getCategory().getName())) {
				br.close();
				page = 1;
				category = feature.getCategory().getName();
				br = new BufferedWriter(new FileWriter(new File(args[1] + "_" + category + "_" + page + ".tsv")));
				br.write("name" + SEPERATOR + "informationsgewinn" + System.lineSeparator());
				c = 0;
			}
			if (c == 50) {
				br.close();
				page++;
				br = new BufferedWriter(new FileWriter(new File(args[1] + "_" + category + "_" + page + ".tsv")));
				br.write("name" + SEPERATOR + "informationsgewinn" + System.lineSeparator());
				c = 0;
			}
			br.write(feature.getName() + SEPERATOR
					+ String.format("%.6f", eval.evaluateAttribute(feature.getAttribute().index()))
					+ System.lineSeparator());
			c++;
		}
		br.close();
	}
	
	private static void saveArff(Instances dataSet) throws Exception {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataSet);
		saver.setFile(new File("/Users/kater/Desktop/eval/all.arff"));
		
		saver.writeBatch();
	}
}
