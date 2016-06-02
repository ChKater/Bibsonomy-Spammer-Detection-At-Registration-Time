package de.luh.chkater.spammerdetection.regLog;

/**
 * model class for Keyboard statistic
 *
 * @author kater
 */
public class KeyboardStatistic {

	private double minDwellTime;
	private double maxDwellTime;
	private double meanDwellTime;
	
	private double minFlightTime;
	private double maxFlightTime;
	private double meanFlightTime;
	
	/**
	 * 
	 */
	public KeyboardStatistic() {
		super();
	}

	/**
	 * @param minDwellTime
	 * @param maxDwellTime
	 * @param meanDwellTime
	 * @param minFlightTime
	 * @param maxFlightTime
	 * @param meanFlightTime
	 */
	public KeyboardStatistic(double minDwellTime, double maxDwellTime, double meanDwellTime, double minFlightTime,
			double maxFlightTime, double meanFlightTime) {
		super();
		this.minDwellTime = minDwellTime;
		this.maxDwellTime = maxDwellTime;
		this.meanDwellTime = meanDwellTime;
		this.minFlightTime = minFlightTime;
		this.maxFlightTime = maxFlightTime;
		this.meanFlightTime = meanFlightTime;
	}

	/**
	 * @return the minDwellTime
	 */
	public double getMinDwellTime() {
		return this.minDwellTime;
	}

	/**
	 * @param minDwellTime the minDwellTime to set
	 */
	public void setMinDwellTime(double minDwellTime) {
		this.minDwellTime = minDwellTime;
	}

	/**
	 * @return the maxDwellTime
	 */
	public double getMaxDwellTime() {
		return this.maxDwellTime;
	}

	/**
	 * @param maxDwellTime the maxDwellTime to set
	 */
	public void setMaxDwellTime(double maxDwellTime) {
		this.maxDwellTime = maxDwellTime;
	}

	/**
	 * @return the meanDwellTime
	 */
	public double getMeanDwellTime() {
		return this.meanDwellTime;
	}

	/**
	 * @param meanDwellTime the meanDwellTime to set
	 */
	public void setMeanDwellTime(double meanDwellTime) {
		this.meanDwellTime = meanDwellTime;
	}

	/**
	 * @return the minFlightTime
	 */
	public double getMinFlightTime() {
		return this.minFlightTime;
	}

	/**
	 * @param minFlightTime the minFlightTime to set
	 */
	public void setMinFlightTime(double minFlightTime) {
		this.minFlightTime = minFlightTime;
	}

	/**
	 * @return the maxFlightTime
	 */
	public double getMaxFlightTime() {
		return this.maxFlightTime;
	}

	/**
	 * @param maxFlightTime the maxFlightTime to set
	 */
	public void setMaxFlightTime(double maxFlightTime) {
		this.maxFlightTime = maxFlightTime;
	}

	/**
	 * @return the meanFlightTime
	 */
	public double getMeanFlightTime() {
		return this.meanFlightTime;
	}

	/**
	 * @param meanFlightTime the meanFlightTime to set
	 */
	public void setMeanFlightTime(double meanFlightTime) {
		this.meanFlightTime = meanFlightTime;
	}
	
	
	

}
