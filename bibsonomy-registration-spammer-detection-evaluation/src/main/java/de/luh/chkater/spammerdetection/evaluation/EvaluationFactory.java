package de.luh.chkater.spammerdetection.evaluation;

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
import de.luh.chkater.spammerdetection.fileconnections.FileDataConnection;
import de.luh.chkater.spammerdetection.fileconnections.LogFileDataConnection;
import de.luh.chkater.spammerdetection.manager.EvaluationSpammerDetectionManager;
import de.luh.chkater.spammerdetection.manager.IDataConnection;
import de.luh.chkater.spammerdetection.utility.ThreadUtility;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.LibLINEAR;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class EvaluationFactory {

	private Map<String, Classifier> classifiers = new HashMap<>();
	private List<String> classifiersNames = new ArrayList<>();

	private Map<String, List<List<FeatureCategory>>> dataSetToPossibleCategories = new HashMap<>();
	private Map<String, IDataConnection> connections = new HashMap<>();

	private int modus = 1;
	private File data, out;
	private int numberOfThreads = Runtime.getRuntime().availableProcessors();

	private EvaluationResult result;
	private int currentClassifier;

	private int featureSelection;

	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		EvaluationFactory a = new EvaluationFactory();
		
		for (int i = 0; i < args.length; i++) {
			String current = args[i];
			switch (current) {
			case "-out":
				a.out = new File(args[i+1]);
				i++;
				break;
			case "-user":
				a.data = new File(args[i+1]);
				i++;
				break;
			case "-modus":
				a.modus = Integer.parseInt(args[i+1]);
				i++;
				break;
			case "-threads":
				a.numberOfThreads = Integer.parseInt(args[i+1]);
				i++;
				break;
			default:
				printUsagae();
				System.exit(1);
				break;
			}
		}
		
		a.setClassifier();
		a.setDataSets();
		a.setFeatureSelection();
		a.run();
	}

	/**
	 * 
	 */
	private static void printUsagae() {
		System.out.println("Usage: [optionname optionparameter]...");
		System.out.println("Possible Options:");
		System.out.println("-moudus\tWhat you want to evaluate");
		System.out.println("-user\t file with user data in tsv format");
		System.out.println("-out\t output directory");
		System.out.println("-threads max. number of Threads to use");
		
		System.out.println("Possible modus values:");
		System.out.println("modus == 1:\tNormal userdata evaluation on Language, Environment, Keyboard, Population");
		System.out.println("modus == 2:\tLog userdata evaluation on Language, Environment, Keyboard Population and Language, Environment, Keyboard Population, Log");
		System.out.println("modus == 3: Reg_Log userdata evaluation on Language, Environment, Keyboard Population and Language, Environment, Keyboard Population, Interaction ");
		System.out.println("modus == 4: Normal userdata evaluation on Language, Environment, Keyboard Population with featureselection ");
		System.out.println("modus == 5: Normal  userdata evaluation on Language, Environment, Keyboard Population with CostSensitive Classifier");
	}

	private void setClassifier() throws Exception {
		classifiers.clear();
		Logistic logistic = new Logistic();
		classifiers.put("Logistic Regression", logistic);
		NaiveBayes nb = new NaiveBayes();
		classifiers.put("Naive Bayes", nb);
//		LibLINEAR svm = new LibLINEAR();
//		svm.setNormalize(true);
//		classifiers.put("SVM", svm);
//		J48 j48 = new J48();
//		classifiers.put("J48", j48);
		if (modus == 5) {
			List<String> keyset = new ArrayList<>(classifiers.keySet());
			for (String key : keyset) {
				for (int i = 1; i <= 10; i++) {
					FilteredClassifier fc = new FilteredClassifier();
					CostSensitiveClassifier csc = new CostSensitiveClassifier();
					CostMatrix matrix = new CostMatrix(2);
					matrix.setElement(0, 0, 0);
					matrix.setElement(1, 1, 0);
					matrix.setElement(0, 1, i);
					matrix.setElement(1, 0, 1);
					csc.setCostMatrix(matrix);
					csc.setClassifier(AbstractClassifier.makeCopy(classifiers.get(key)));
					fc.setClassifier(csc);
					ReplaceMissingValues rmv = new ReplaceMissingValues();
					fc.setFilter(rmv);
					classifiers.put(key + " (CostSensitive 1 : " + i +")", fc);
				}

			}
		}

	}

	private void setDataSets() throws IOException {

		if (modus == 1 || modus == 4 || modus == 5) {
			dataSetToPossibleCategories.put("benutzertabelle", Arrays.asList(Arrays.asList(FeatureCategory.LANGUAGE,
					FeatureCategory.ENVIRONMENT, FeatureCategory.KEYBOARD, FeatureCategory.POPULATION_BASED)));
			connections.put("benutzertabelle", new FileDataConnection(data));
		}
		if (modus == 2) {
			dataSetToPossibleCategories.put("log", Arrays.asList(
					Arrays.asList(FeatureCategory.LANGUAGE, FeatureCategory.ENVIRONMENT, FeatureCategory.KEYBOARD,
							FeatureCategory.POPULATION_BASED, FeatureCategory.LOG),
					Arrays.asList(FeatureCategory.LANGUAGE, FeatureCategory.ENVIRONMENT, FeatureCategory.KEYBOARD,
							FeatureCategory.POPULATION_BASED),
					Arrays.asList(FeatureCategory.LOG)));
			connections.put("log", new LogFileDataConnection(data));
		}
		if (modus == 3) {
			dataSetToPossibleCategories.put("interaction", Arrays.asList(
					Arrays.asList(FeatureCategory.LANGUAGE, FeatureCategory.ENVIRONMENT, FeatureCategory.KEYBOARD,
							FeatureCategory.POPULATION_BASED, FeatureCategory.INTERACTION),
					Arrays.asList(FeatureCategory.LANGUAGE, FeatureCategory.ENVIRONMENT, FeatureCategory.KEYBOARD,
							FeatureCategory.POPULATION_BASED),
					Arrays.asList(FeatureCategory.INTERACTION)));
			connections.put("interaction", new FileDataConnection(data, 13));
		}

	}

	private void setFeatureSelection() {
		if (modus == 4) {
			featureSelection = 11;
		} else {
			featureSelection = 1;
		}
	}

	public void run() throws Exception {

		for (String datasetToInspect : connections.keySet()) {
			final List<List<FeatureCategory>> possibleCategories = dataSetToPossibleCategories.get(datasetToInspect);
			final IDataConnection connection = connections.get(datasetToInspect);
			final String dataset = datasetToInspect;
			for (List<FeatureCategory> categories : possibleCategories) {
				String categoriesStrTmp = "(" + categories.get(0).getName();
				for (int i = 1; i < categories.size(); i++) {
					categoriesStrTmp += ("_" + categories.get(i).getName());
				}
				final String categoriesStr = categoriesStrTmp + ")";

				List<AbstractFeature> allFeatures = FeatureFactory.getInstance()
						.getFeatures(FeatureFactory.getInstance().getFeaturesFromCategoryList(categories));
				final EvaluationSpammerDetectionManager allManager = new EvaluationSpammerDetectionManager(connection,
						allFeatures);

				for (int featureSelectionOption = 0; featureSelectionOption < featureSelection; featureSelectionOption++) {

					result = new EvaluationResult();

					EvaluationSpammerDetectionManager managerTmp = null;
					try {
						managerTmp = new EvaluationSpammerDetectionManager(connection,
								selectFeatures(allManager.getInstances(), featureSelectionOption, allFeatures));
					} catch (Exception e1) {
						e1.printStackTrace();
						System.exit(1);
					}
					final EvaluationSpammerDetectionManager manager = managerTmp;
					final int featureSelectionFinal = featureSelectionOption;
					currentClassifier = -1;
					classifiersNames = new ArrayList<>(classifiers.keySet());
					ThreadUtility.runTaskOnAllThreads(Math.max(6, numberOfThreads / 10), new Runnable() {

						@Override
						public void run() {
							String classifier = null;
							while ((classifier = getClassifier()) != null) {
								try {
									long start = System.currentTimeMillis();
									Evaluation evaluation = manager.evaluate(classifiers.get(classifier));
									long end = System.currentTimeMillis();
									addEvaluation(classifier, evaluation, end - start);
									saveRoc(evaluation,
											new File(out, "roc[categories=" + categoriesStr + "][featureSelection="
													+ featureSelectionFinal + "][dataset=" + dataset + "][classifier="
													+ classifier + "][modus=" + modus + "].tsv"));
								} catch (Exception e) {
									e.printStackTrace();

								}

							}

						}

					});
					try {
						result.saveMean(new File(out, "results" + "[categories=" + categoriesStr + "][featureSelection="
								+ featureSelectionOption + "][dataset=" + dataset + "][modus=" + modus + "].csv"));
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}

				}
			}
		}

	}

	/**
	 * @param allManager
	 * @param featureSelectionOption
	 * @param categories
	 * @return
	 * @throws Exception
	 */
	private List<AbstractFeature> selectFeatures(Instances instances, int featureSelectionOption,
			List<AbstractFeature> features) throws Exception {
		switch (featureSelectionOption) {
		case 0:
			return features;
		case 1:
			return topXIG(features, instances, 10);
		case 2:
			return topXIG(features, instances, 25);
		case 3:
			return topXIG(features, instances, 50);
		case 4:
			return topXIG(features, instances, 75);
		case 5:
			return topXIG(features, instances, 100);
		case 6:
			return topXChiSqu(features, instances, 10);
		case 7:
			return topXChiSqu(features, instances, 25);
		case 8:
			return topXChiSqu(features, instances, 50);
		case 9:
			return topXChiSqu(features, instances, 75);
		case 10:
			return topXChiSqu(features, instances, 100);
		default:
			break;
		}
		return null;
	}

	/**
	 * @param features
	 * @param instances
	 * @param i
	 * @return
	 * @throws Exception
	 */
	private List<AbstractFeature> topXChiSqu(List<AbstractFeature> features, Instances instances, int numberOfFeatures)
			throws Exception {
		if (features.size() < numberOfFeatures) {
			return features;
		}

		ChiSquaredAttributeEval eval = new ChiSquaredAttributeEval();
		eval.buildEvaluator(instances);
		final Map<String, Double> scores = new HashMap<>();
		for (int i = 1; i < features.size(); i++) {
			AbstractFeature feature = features.get(i);
			scores.put(feature.getName(), eval.evaluateAttribute(feature.getAttribute().index()));
		}
		List<String> featureNames = new ArrayList<>(scores.keySet());
		Collections.sort(featureNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return -1 * Double.compare(scores.get(o1), scores.get(o2));
			}
		});

		featureNames.add(0, "spammer");
		featureNames = featureNames.subList(0, numberOfFeatures + 1);
		return FeatureFactory.getInstance().getFeatures(featureNames);
	}

	private List<AbstractFeature> topXIG(List<AbstractFeature> features, Instances instances, int numberOfFeatures)
			throws Exception {
		if (features.size() < numberOfFeatures) {
			return features;
		}
		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		eval.buildEvaluator(instances);
		final Map<String, Double> scores = new HashMap<>();
		for (int i = 1; i < features.size(); i++) {
			AbstractFeature feature = features.get(i);
			scores.put(feature.getName(), eval.evaluateAttribute(feature.getAttribute().index()));
		}
		List<String> featureNames = new ArrayList<>(scores.keySet());
		Collections.sort(featureNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return -1 * Double.compare(scores.get(o1), scores.get(o2));
			}
		});

		featureNames.add(0, "spammer");
		featureNames = featureNames.subList(0, numberOfFeatures + 1);
		return FeatureFactory.getInstance().getFeatures(featureNames);

	}

	private void saveRoc(Evaluation evaluation, File file) throws IOException {
		ThresholdCurve tc = new ThresholdCurve();
		int classIndex = 0;
		Instances curve = tc.getCurve(evaluation.predictions(), classIndex);

		List<Double> tprTmp = new ArrayList<>();
		List<Double> fprTmp = new ArrayList<>();

		for (int i = curve.numInstances() - 1; i >= 0; i--) {
			Instance inst = curve.instance(i);

			tprTmp.add(inst.value(curve.attribute("True Positive Rate")));
			fprTmp.add(inst.value(curve.attribute("False Positive Rate")));

		}
		int numPerPoint = (int) Math.ceil((double) tprTmp.size() / 200.0);
		ArrayList<Double> tpr = new ArrayList<>();
		ArrayList<Double> fpr = new ArrayList<>();
		tpr.add(0.0);
		for (int i = 0; i < tprTmp.size(); i = i + numPerPoint) {
			int j = 0;
			double sum = 0;
			for (j = 0; j < numPerPoint && i + j < tprTmp.size(); j++) {
				sum += tprTmp.get(j + i);
			}
			sum /= j;
			tpr.add(sum);

		}
		tpr.add(1.0);
		fpr.add(0.0);
		for (int i = 0; i < fprTmp.size(); i = i + numPerPoint) {
			int j = 0;
			double sum = 0;
			for (j = 0; j < numPerPoint && i + j < fprTmp.size(); j++) {
				sum += fprTmp.get(j + i);
			}
			sum /= j;
			fpr.add(sum);

		}
		fpr.add(1.0);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		bw.write("False Positive Rate\tTrue Positive Rate" + System.lineSeparator());
		for (int i = 0; i < tpr.size(); i++) {
			bw.write(fpr.get(i) + "\t" + tpr.get(i) + System.lineSeparator());
		}
		bw.close();
	}

	/**
	 * @param classifier
	 * @param evaluate
	 * @param time
	 */
	private synchronized void addEvaluation(String classifier, Evaluation evaluate, long time) {
		result.addEvaluation(classifier, evaluate, time);
	}

	private synchronized String getClassifier() {
		currentClassifier++;
		if (currentClassifier >= classifiersNames.size()) {
			return null;
		}
		return classifiersNames.get(currentClassifier);
	}

}
