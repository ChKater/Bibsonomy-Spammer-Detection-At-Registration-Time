package de.luh.chkater.spammerdetection.feature.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.FeatureCategory;
import de.luh.chkater.spammerdetection.feature.interfaces.NumericFeature;

/**
 * Measures the information suprise of an property as string of an user. 
 *
 * @author kater
 */
public class InformationSuprise extends NumericFeature implements Function<User, Double>{

	private Function<User, String> whatToCount;
	
	private Map<Character, Double> singleProp = new HashMap<>();
	private int singleTotal;
	private Map<String, Double> jointProp = new HashMap<>();
	private int jointTotal;
	
	/**
	 * @param name Name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param whatToCount defines which property of the user should to measure
	 */
	public InformationSuprise(String name, int version, FeatureCategory category, Function<User, String> whatToCount) {
		super(name, version, category, true, null);
		super.transformation = this;
		this.whatToCount = whatToCount;
	}

	
	@Override
	public Double apply(User user) {
		String apply = whatToCount.apply(user);
		if(apply == null){
			return null;
		}
		String word = WORD_START + apply + WORD_END;
		double prop = 1;
		for (int i = 1; i < word.length(); i++) {
			char prev = word.charAt(i-1);
			char cur = word.charAt(i);
			double conditionalProbability = getConditionalProbability(cur, prev);
			prop *= conditionalProbability;
		}
		double infoSupr = -1 * (Math.log(prop) / Math.log(2));
		if(Double.isNaN(infoSupr) || Double.isInfinite(infoSupr)){
			return null;
		}
		return new Double(infoSupr);
	}
	
	
	@Override
	public void setUsers(List<User> users) {
		singleProp.clear();
		jointProp.clear();
		singleTotal = 0;
		jointTotal = 0;
		for (User user : users) {
			String apply = whatToCount.apply(user);
			if(apply == null){
				continue;
			}
			String word = WORD_START + apply + WORD_END;
			for (int i = 0; i < word.length() - 1; i++) {
				char cur = word.charAt(i);
				char next = word.charAt(i+1);
				inc(cur);
				inc(cur, next);
			}
			inc(WORD_END);
		}
//		List<Character> singleKeys = new ArrayList<>(singleProp.keySet());
//		for (Character symbol : singleKeys) {
//			singleProp.put(symbol, singleProp.get(symbol) / singleTotal);
//		}
//		List<String> jointKeys = new ArrayList<>(jointProp.keySet());
//		for (String bigram : jointKeys) {
//			jointProp.put(bigram, jointProp.get(bigram) / jointTotal);
//		}
	}
	
	private static final char WORD_START = '\u2605';
	private static final char WORD_END = '\u2022';



	private double getConditionalProbability(char symbol, char given) {
		return jointProp(symbol, given) / singleProp(given);
	}
	
	private double jointProp(char symbol, char given){
		Double prop = jointProp.get(getBigram(symbol, given));
		return prop == null ? 0.0 : prop.doubleValue() / jointTotal;
	}
	
	private double singleProp(char symbol){
		Double prop = singleProp.get(symbol);
		return prop == null ? 0.0 : prop.doubleValue() / singleTotal;
	} 


	private void inc(char symbol) {
		singleTotal++;
		Double count = singleProp.get(symbol);
		if(count == null){
			singleProp.put(symbol, 1.0);
		} else {
			singleProp.put(symbol, count + 1);
		}
	}

	private void inc(char c1, char c2) {
		String bigram = getBigram(c1, c2);
		jointTotal++;
		Double count = jointProp.get(bigram);
		if(count == null){
			jointProp.put(bigram, 1.0);
		} else {
			jointProp.put(bigram, count + 1);
		}
	}

	private static String getBigram(char c1, char c2) {
		if(c1 < c2){
			return String.valueOf(c1) + String.valueOf(c2);
		}
		return String.valueOf(c2) + String.valueOf(c1);
	}

	@Override
	public AbstractFeature newInstance() {
		InformationSuprise informationSuprise = new InformationSuprise(getName(), version, category, whatToCount);
		return informationSuprise;
		
	}
}
