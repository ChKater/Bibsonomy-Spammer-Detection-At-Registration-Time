package de.luh.chkater.weka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.utility.ThreadUtility;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnassignedClassException;
import weka.core.converters.ArffSaver;

/** 
 *
 * @author Christian Kater
 */
public class PopulationbaseEvaluation extends weka.classifiers.Evaluation {

	/**
	 * @param data
	 * @throws Exception
	 */
	public PopulationbaseEvaluation(Instances data) throws Exception {
		super(data);
		m_delegate = new MyPopulationbaseEvaluation(data);
	}

	public void crossValidateModel(Classifier classifier, Instances data, int numFolds, Random random,
			Map<Integer, String> positionToUser, Function<String, User> getUser, List<AbstractFeature> features,
			Object... forPredictionsPrinting) throws Exception {
		MyPopulationbaseEvaluation mPopEv = (MyPopulationbaseEvaluation) m_delegate;
		mPopEv.crossValidateModel(classifier, data, numFolds, random, positionToUser, getUser, features,
				forPredictionsPrinting);
	}

	private boolean error = false;

	private class MyPopulationbaseEvaluation extends Evaluation {

		/**
		 * @param data
		 * @throws Exception
		 */
		public MyPopulationbaseEvaluation(Instances data) throws Exception {
			super(data);
		}

		public void crossValidateModel(final Classifier classifier, Instances data, final int numFolds,
				final Random random, Map<Integer, String> positionToUser, final Function<String, User> getUser,
				final List<AbstractFeature> features, final Object... forPredictionsPrinting) throws Exception {
			// Make a copy of the data and positonTouser we can reorder

			final Instances cData = new Instances(data);

			final HashMap<Integer, String> cPositionToUser = new HashMap<>(positionToUser);
			randomize(cData, random, cPositionToUser);
			if (data.classAttribute().isNominal()) {
				stratify(cData, numFolds, cPositionToUser);
			}

			// We assume that the first element is a
			// weka.classifiers.evaluation.output.prediction.AbstractOutput
			// object
			AbstractOutput classificationOutput = null;
			if (forPredictionsPrinting.length > 0) {
				// print the header first
				classificationOutput = (AbstractOutput) forPredictionsPrinting[0];
				classificationOutput.setHeader(data);
				classificationOutput.printHeader();
			}

			ThreadUtility.runTaskOnAllThreads(3,new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
							// List<AbstractFeature> featureCopy =
							// FeatureFactory.getInstance().deepCopy(features);
							int i = getFold();
							if (i >= numFolds) {
								break;
							}
							Instances train = cData.trainCV(numFolds, i);
							final List<User> users = new ArrayList<>();
							for (int j = 0; j < train.numInstances(); j++) {
								users.add(getUser.apply(cPositionToUser
										.get(transformTrainPosition(j, numFolds, i, train.numInstances()))));
							}
							for (final AbstractFeature feature : features) {

								if (feature.isNeedAllUser()) {
									feature.setUsers(users);
									for (int j = 0; j < users.size(); j++) {
										feature.apply(users.get(j), train.instance(j));
									}
								}
							}
							train.randomize(random);
							setPriors(train);
							Classifier copiedClassifier = AbstractClassifier.makeCopy(classifier);
							copiedClassifier.buildClassifier(train);
							Instances test = cData.testCV(numFolds, i);
							users.clear();
							for (int j = 0; j < test.numInstances(); j++) {
								users.add(getUser.apply(cPositionToUser
										.get(transformTestPosition(j, numFolds, i, test.numInstances()))));
							}
							for (final AbstractFeature feature : features) {
								if (feature.isNeedAllUser()) {
									for (int j = 0; j < users.size(); j++) {
										feature.apply(users.get(j), test.instance(j));
									}
								}
							}
							evaluateModelSynchronized(copiedClassifier, test, forPredictionsPrinting);
						}
					} catch (Exception e) {
						e.printStackTrace();
						error = true;
					}
				}
			});
			if (error) {
				throw new Exception("error");
			}
			m_NumFolds = numFolds;
		}

		private int currentFold = -1;

		private synchronized int getFold() {
			currentFold++;
			return currentFold;
		}

		private synchronized double[] evaluateModelSynchronized(Classifier classifier, Instances data,
				Object... forPredictionsPrinting) throws Exception {
			return evaluateModel(classifier, data, forPredictionsPrinting);
		}

		private void stratify(Instances data, int numFolds, Map<Integer, String> positionToUser) {
			if (numFolds <= 1) {
				throw new IllegalArgumentException("Number of folds must be greater than 1");
			}
			if (data.classIndex() < 0) {
				throw new UnassignedClassException("Class index is negative (not set)!");
			}
			if (data.classAttribute().isNominal()) {

				// sort by class
				int index = 1;
				while (index < numInstances()) {
					Instance instance1 = data.instance(index - 1);
					for (int j = index; j < numInstances(); j++) {
						Instance instance2 = data.instance(j);
						if ((instance1.classValue() == instance2.classValue())
								|| (instance1.classIsMissing() && instance2.classIsMissing())) {
							data.swap(index, j);
							swapPositions(positionToUser, index, j);
							index++;
						}
					}
					index++;
				}
				stratStep(data, numFolds, positionToUser);
			}
		}

		protected void stratStep(Instances data, int numFolds, Map<Integer, String> positionToUser) {

			Map<Integer, String> copy = new HashMap<>(positionToUser);
			positionToUser.clear();
			FastVector newVec = new FastVector(data.numInstances());
			int elements = 0;
			int start = 0, j;

			// create stratified batch
			while (newVec.size() < data.numInstances()) {
				j = start;
				while (j < data.numInstances()) {
					newVec.addElement(data.instance(j));
					positionToUser.put(elements, copy.get(j));
					elements++;
					j = j + numFolds;
				}
				start++;
			}
			data.delete();
			for (int i = 0; i < newVec.size(); i++) {
				data.add((Instance) newVec.elementAt(i));
			}

		}

		private void randomize(Instances data, Random random, Map<Integer, String> positionToUser) {
			for (int j = data.numInstances() - 1; j > 0; j--) {
				int nextInt = random.nextInt(j + 1);
				data.swap(j, nextInt);
				swapPositions(positionToUser, j, nextInt);
			}
		}

		/**
		 * @param positionToUser
		 * @param j
		 * @param nextInt
		 */
		private void swapPositions(Map<Integer, String> toSwitch, int first, int second) {
			String firstStr = toSwitch.get(first);
			String secondStr = toSwitch.get(second);
			toSwitch.put(first, secondStr);
			toSwitch.put(second, firstStr);

		}

	}

	private static int transformTrainPosition(int position, int numFolds, int numFold, int numInstances) {
		int numInstForFold, first, offset;

		numInstForFold = numInstances / numFolds;
		if (numFold < numInstances % numFolds) {
			numInstForFold++;
			offset = numFold;
		} else {
			offset = numInstances % numFolds;
		}
		first = numFold * (numInstances / numFolds) + offset;
		if (position < first) {
			return position;
		}
		return position + numInstForFold;
	}

	private static int transformTestPosition(int position, int numFolds, int numFold, int numInstances) {
		int first, offset;
		if (numFold < numInstances % numFolds) {
			offset = numFold;
		} else {
			offset = numInstances % numFolds;
		}
		first = numFold * (numInstances / numFolds) + offset;
		return position + first;
	}

}
