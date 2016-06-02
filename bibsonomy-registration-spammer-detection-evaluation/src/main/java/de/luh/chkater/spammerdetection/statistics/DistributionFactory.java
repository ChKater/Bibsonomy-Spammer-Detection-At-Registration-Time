package de.luh.chkater.spammerdetection.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.omg.PortableInterceptor.INACTIVE;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import weka.core.Instances;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class DistributionFactory {

	public static Map<String, Double> maxCCDFDiffs = new HashMap<>();
	public static Map<String, Integer> spammerN = new HashMap<>();
	public static Map<String, Integer> nonSpammerN = new HashMap<>();

	private static final int GRID_SIZE = 500;
	private static final String SEPARATOR = "\t";

	public static void saveCCDF(Instances instances, AbstractFeature feature, File outDir) throws IOException {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_NORMAL;

		int featureIndex = feature.getAttribute().index();

		
		List<Double>[] values = new ArrayList[] { new ArrayList<>(instances.numInstances()),
				new ArrayList<>(instances.numInstances()) };
		for (int i = 0; i < instances.size(); i++) {
			if (instances.instance(i).isMissing(featureIndex)) {
				continue;
			}
			double value = instances.instance(i).value(featureIndex);
			int spammer = (int) instances.instance(i).value(0);
			values[spammer].add(value);
			
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}

		}
		
 
		nonSpammerN.put(feature.getName(), values[0].size());
		spammerN.put(feature.getName(), values[1].size());

		double diff = max - min;
		double step = diff / GRID_SIZE;

		Map<Double, Double>[] distr = new HashMap[] { new HashMap<>(), new HashMap<>() };
		double border = 0.01;
		if(feature.getName().equals("homepageInfoSupr")	 || feature.getName().endsWith("Ratio")){
			border = 0.00001;
		}
		

		for (int i = 0; i < 2; i++) {
			for (double current = min; current <= max; current += step) {
				double ccdf = ccdf(current, values[i]);
				if (ccdf < border) {
					break;
				}
				distr[i].put(current, ccdf);
			}
		}
		double maxCCDFDiff = Double.MIN_NORMAL;
		for (double current = min; current <= max; current += step) {
			double ccdf0 = ccdf(current, values[0]);
			double ccdf1 = ccdf(current, values[1]);
			if (ccdf0 <= border || ccdf1 <= border) {
				break;
			}
			double ccdfDiff = Math.abs(ccdf0 - ccdf1);
			if (ccdfDiff > maxCCDFDiff) {
				maxCCDFDiff = ccdfDiff;
			}
		}

		maxCCDFDiffs.put(feature.getName(), maxCCDFDiff);

		write(distr[1], new File(outDir, feature.getName() + "_spammer.tsv"));
		write(distr[0], new File(outDir, feature.getName() + "_non_spammer.tsv"));

	}

	public static void saveIntegerDistribution(Instances instances, AbstractFeature feature, File outDir)
			throws IOException {

		Map<Integer, Double>[] distr = new HashMap[] { new HashMap<>(), new HashMap<>() };
		int featureIndex = feature.getAttribute().index();

		int[] total = new int[] { 0, 0 };

		for (int i = 0; i < instances.size(); i++) {
			if (instances.instance(i).isMissing(featureIndex)) {
				continue;
			}
			int value = (int) (instances.instance(i).value(featureIndex));
			int spammer = (int) instances.instance(i).value(0);
			increaseCount(distr[spammer], value);
			total[spammer]++;

		}

		for (int i = 0; i < 2; i++) {
			for (Integer key : distr[i].keySet()) {
				distr[i].put(key, distr[i].get(key) / total[i]);
			}
		}

		write(distr[1], new File(outDir, feature.getName() + "-integer_spammer.tsv"));
		write(distr[0], new File(outDir, feature.getName() + "-integer_non_spammer.tsv"));

	}

	public static void saveKolmogorowSmirnow(File out) throws IOException {
		List<String> features = new ArrayList<>(maxCCDFDiffs.keySet());
		Collections.sort(features);
		String sep = ";";
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		bw.write("featurename;val;k5;k10;k20" + System.lineSeparator());
		for (String feature : features) {
			try {
				Integer n = spammerN.get(feature);
				Integer m = nonSpammerN.get(feature);
				double factor = Math.sqrt((n * m) / (n + m));
				double test = factor * maxCCDFDiffs.get(feature);
				double k0_1 = Math.sqrt(Math.log(2 / 0.001) / 2);
				bw.write(feature + sep + test + sep + (test > k0_1 ? "X" : "-") + System.lineSeparator());
			} catch (Exception e) {
				System.out.println("error: " + feature);
			}
		}
		bw.close();
	}

	public static double ccdf(double toInspect, List<Double> values) {
		int count = 0;
		for (Double value : values) {
			if (value > toInspect) {
				count++;
			}
		}
		return (double) count / (double) values.size();
	}

	public static void saveNominalDistribution(Instances instances, AbstractFeature feature, File outDir)
			throws IOException {
		int featureIndex = feature.getAttribute().index();

		Map<String, Double> spammerDistr = new HashMap<>();
		Map<String, Double> nonSpammerDistr = new HashMap<>();

		int spammerTotal = 0;
		int nonSpammerTotal = 0;

		for (int i = 0; i < instances.size(); i++) {
			if (instances.instance(i).isMissing(featureIndex)) {
				continue;
			}
			String value = instances.instance(i).stringValue(featureIndex);
			if (value.equals("?")) {
				continue;
			}

			boolean spammer = instances.instance(i).value(0) == 1;

			if (spammer) {
				spammerTotal++;
				increaseCount(spammerDistr, value);
			} else {
				nonSpammerTotal++;
				increaseCount(nonSpammerDistr, value);
			}
		}

		for (String key : spammerDistr.keySet()) {
			spammerDistr.put(key, spammerDistr.get(key) / spammerTotal);
		}

		for (String key : nonSpammerDistr.keySet()) {
			nonSpammerDistr.put(key, nonSpammerDistr.get(key) / nonSpammerTotal);
		}

		write(spammerDistr, new File(outDir, feature.getName() + "_spammer.tsv"));
		write(nonSpammerDistr, new File(outDir, feature.getName() + "_non_spammer.tsv"));

	}

	private static <T> void increaseCount(Map<T, Double> map, T value) {

		Double count = map.get(value);
		if (count == null) {
			count = 0.0;
		}
		map.put(value, count + 1);
	}

	private static <T extends Comparable<T>> void write(Map<T, Double> map, File file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		List<T> keys = new LinkedList<>(map.keySet());
		Collections.sort(keys, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				try {
					return Integer.compare(Integer.parseInt(o1.toString()), Integer.parseInt(o2.toString()));
				} catch (NumberFormatException e) {
					return o1.compareTo(o2);
				}
			}
		});
		bw.write("Value" + SEPARATOR + "Occ" + System.lineSeparator());
		for (T key : keys) {
			bw.write(key + SEPARATOR + map.get(key) + System.lineSeparator());
		}

		bw.close();
	}

}
