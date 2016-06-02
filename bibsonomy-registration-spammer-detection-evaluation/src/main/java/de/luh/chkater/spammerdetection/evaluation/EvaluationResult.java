package de.luh.chkater.spammerdetection.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Evaluation;



/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class EvaluationResult {

	private static final String SEPARATOR = ";";
	private Map<String, List<Double>[]> precision;
	private Map<String, List<Double>[]> recall;
	private Map<String, List<Double>[]> f1;
	private Map<String, List<Double>[]> auc;
	private Map<String, List<Double>[]> correct;
	private Map<String, List<Double>[]> wrong;
	private Map<String, List<Double>[]> time;
	

	public EvaluationResult() {
		precision = new HashMap<>();
		recall = new HashMap<>();
		f1 = new HashMap<>();
		auc = new HashMap<>();
		correct = new HashMap<>();
		wrong = new HashMap<>();
		time = new HashMap<>();
	}

	/**
	 * @param evaluation
	 */
	public void addEvaluation(String algorithm, Evaluation evaluation, long timeDiff) {
		for (int i = 0; i < 2; i++) {
			addValue(precision, algorithm, i, evaluation.precision(i));
			addValue(recall, algorithm, i, evaluation.recall(i));
			addValue(f1, algorithm, i, evaluation.fMeasure(i));
			addValue(auc, algorithm, i, evaluation.areaUnderROC(i));
			addValue(correct, algorithm, i, evaluation.confusionMatrix()[i][i]);
			addValue(wrong, algorithm, i, evaluation.confusionMatrix()[i][1 - i]);
			addValue(time, algorithm, i, timeDiff);
		}
		addValue(precision, algorithm, 2, evaluation.weightedPrecision());
		addValue(recall, algorithm, 2, evaluation.weightedRecall());
		addValue(f1, algorithm, 2, evaluation.weightedFMeasure());
		addValue(auc, algorithm, 2, evaluation.weightedAreaUnderROC());
		addValue(correct, algorithm, 2, evaluation.confusionMatrix()[0][0] + evaluation.confusionMatrix()[1][1]);
		addValue(wrong, algorithm, 2, evaluation.confusionMatrix()[0][1] + evaluation.confusionMatrix()[1][0]);
		addValue(time, algorithm, 2, timeDiff);
		System.out.println(algorithm + ": " + evaluation.areaUnderROC(0));
	}
	
	private void addValue(Map<String, List<Double>[]>map, String algorithm, int classIndex, double value){
		if(!map.containsKey(algorithm)){
			List<Double>[] insert = new List[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
			map.put(algorithm, insert);
		}
		map.get(algorithm)[classIndex].add(value);
	}

	public void saveMean(File out) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		bw.write("alg" + SEPARATOR + "class" + SEPARATOR + "prec" + SEPARATOR + "rec" + SEPARATOR + "f1" + SEPARATOR + "auc" + SEPARATOR
				+ "correct" + SEPARATOR + "wrong" + SEPARATOR + "time"+ System.lineSeparator());
		for (String algorithm : precision.keySet()) {
			for (int i = 0; i < 3; i++) {
				StringBuilder sb = new StringBuilder();
				sb.append(algorithm);
				sb.append(SEPARATOR);
				sb.append((i == 1) ? "Spammer" : ((i == 2) ? "Gesamt" : "Nicht-Spammer"));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(precision.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(recall.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(f1.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(auc.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", mean(correct.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", mean(wrong.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", mean(time.get(algorithm)[i])));
				sb.append(System.lineSeparator());
				bw.write(sb.toString());
			}
		}
		bw.close();
	}
	
	public void saveAll(File out) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		StringBuilder hb = new StringBuilder();
		hb.append("alg");
		hb.append(SEPARATOR);
		hb.append("class");
		hb.append(SEPARATOR);
		hb.append("min_prec");
		hb.append(SEPARATOR);
		hb.append("max_prec");
		hb.append(SEPARATOR);
		hb.append("mean_prec");
		hb.append(SEPARATOR);
		hb.append("std_prec");
		hb.append(SEPARATOR);
		hb.append("min_rec");
		hb.append(SEPARATOR);
		hb.append("max_rec");
		hb.append(SEPARATOR);
		hb.append("mean_rec");
		hb.append(SEPARATOR);
		hb.append("std_rec");
		hb.append(SEPARATOR);
		hb.append("min_f1");
		hb.append(SEPARATOR);
		hb.append("max_f1");
		hb.append(SEPARATOR);
		hb.append("mean_f1");
		hb.append(SEPARATOR);
		hb.append("std_f1");
		hb.append(SEPARATOR);
		hb.append("min_auc");
		hb.append(SEPARATOR);
		hb.append("max_auc");
		hb.append(SEPARATOR);
		hb.append("mean_auc");
		hb.append(SEPARATOR);
		hb.append("std_auc");
		hb.append(SEPARATOR);
		hb.append("min_correct");
		hb.append(SEPARATOR);
		hb.append("max_correct");
		hb.append(SEPARATOR);
		hb.append("mean_correct");
		hb.append(SEPARATOR);
		hb.append("std_correct");
		hb.append(SEPARATOR);
		hb.append("min_wrong");
		hb.append(SEPARATOR);
		hb.append("max_wrong");
		hb.append(SEPARATOR);
		hb.append("mean_wrong");
		hb.append(SEPARATOR);
		hb.append("std_wrong");
		hb.append(SEPARATOR);
		hb.append("min_time");
		hb.append(SEPARATOR);
		hb.append("max_time");
		hb.append(SEPARATOR);
		hb.append("mean_time");
		hb.append(SEPARATOR);
		hb.append("std_time");
		hb.append(System.lineSeparator());
		
		
		bw.write(hb.toString());
		for (String algorithm : precision.keySet()) {
			for (int i = 0; i < 3; i++) {
				StringBuilder sb = new StringBuilder();
				sb.append(algorithm);
				sb.append(SEPARATOR);
				sb.append((i == 1) ? "Spammer" : ((i == 2) ? "Gesamt" : "Nicht-Spammer"));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", min(precision.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", max(precision.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(precision.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", std(precision.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", min(recall.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", max(recall.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(recall.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", std(recall.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", min(f1.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", max(f1.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(f1.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", std(f1.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", min(auc.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", max(auc.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", mean(auc.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.2f", std(auc.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", min(correct.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", max(correct.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", mean(correct.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", std(correct.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", min(wrong.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", max(wrong.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", mean(wrong.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", std(wrong.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", min(time.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", max(time.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", mean(time.get(algorithm)[i])));
				sb.append(SEPARATOR);
				sb.append(String.format("%.0f", std(time.get(algorithm)[i])));
				sb.append(System.lineSeparator());
				bw.write(sb.toString());
			}
		}
		bw.close();
	}

	public double min(List<Double> list) {
		Double min = Double.MAX_VALUE;
		for (Double current : list) {
			if (current < min) {
				min = current;
			}
		}
		return min;
	}

	public double max(List<Double> list) {
		Double max = Double.MIN_NORMAL;
		for (Double current : list) {
			if (current > max) {
				max = current;
			}
		}
		return max;
	}

	public double mean(List<Double> list) {
		Double mean = 0.0;
		for (Double current : list) {
			mean += current;
		}
		return mean / list.size();
	}

	public double std(List<Double> list) {
		double mean = mean(list);
		double std = 0.0;
		for (Double current : list) {
			double deviation = current - mean;
			std += (deviation * deviation);
		}
		return Math.sqrt(std / list.size());
	}

}
