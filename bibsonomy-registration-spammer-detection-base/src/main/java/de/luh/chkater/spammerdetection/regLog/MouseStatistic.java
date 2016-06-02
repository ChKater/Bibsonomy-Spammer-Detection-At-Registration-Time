package de.luh.chkater.spammerdetection.regLog;

/**
 * Model class for mouse statistics
 *
 * @author kater
 */
public class MouseStatistic {

	private double[] minVelocity;
	private double[] maxVelocity;
	private double[] meanVelocity;

	private double[] minAcceleration;
	private double[] maxAcceleration;
	private double[] meanAcceleration;
	
	private double[] movedDistance;

	private double mouseMoveTime;
	
	private double minPauseNClick;
	private double maxPauseNClick;
	private double meanPauseNClick;

	private double minClickTime;
	private double maxClickTime;
	private double meanClickTime;

	
	/**
	 * 
	 */
	public MouseStatistic() {
		super();
	}
	
	
	/**
	 * @param minVelocity
	 * @param maxVelocity
	 * @param meanVelocity
	 * @param minAcceleration
	 * @param maxAcceleration
	 * @param meanAcceleration
	 * @param movedDistance
	 * @param mouseMoveTime
	 * @param minPauseNClick
	 * @param maxPauseNClick
	 * @param meanPauseNClick
	 * @param minClickTime
	 * @param maxClickTime
	 * @param meanClickTime
	 * @param minTraveledDist
	 * @param maxTraveledDist
	 * @param meanTraveledDist
	 * @param minAngleOfCurvature
	 * @param maxAngleOfCurvature
	 * @param meanAngleOfCurvature
	 * @param minCurvatureDistance
	 * @param maxCurvatureDistance
	 * @param meanCurvatureDistance
	 */
	public MouseStatistic(double[] minVelocity, double[] maxVelocity, double[] meanVelocity, double[] minAcceleration,
			double[] maxAcceleration, double[] meanAcceleration, double[] movedDistance, double mouseMoveTime,
			double minPauseNClick, double maxPauseNClick, double meanPauseNClick, double minClickTime,
			double maxClickTime, double meanClickTime) {
		super();
		this.minVelocity = norm(minVelocity);
		this.maxVelocity = norm(maxVelocity);
		this.meanVelocity = norm(meanVelocity);
		this.minAcceleration = norm(minAcceleration);
		this.maxAcceleration = norm(maxAcceleration);
		this.meanAcceleration = norm(meanAcceleration);
		this.movedDistance = norm(movedDistance);
		this.mouseMoveTime = mouseMoveTime;
		if(!Double.isNaN(meanPauseNClick)){
			this.minPauseNClick = minPauseNClick;
			this.maxPauseNClick = maxPauseNClick;
			this.meanPauseNClick = meanPauseNClick;
		} else {
			this.minPauseNClick = 0;
			this.maxPauseNClick = 0;
			this.meanPauseNClick = 0;
		}
		if(!Double.isNaN(meanClickTime)){
			this.minClickTime = minClickTime;
			this.maxClickTime = maxClickTime;
			this.meanClickTime = meanClickTime;
		} else {
			this.minClickTime = 0;
			this.maxClickTime = 0;
			this.meanClickTime = 0;
		}

		
		
	
		
		
	}
	
	public static double[] norm(double[] val){
		double max = Double.MIN_NORMAL;
		double[] normed = new double[val.length];
		for (int i = 0; i < val.length; i++) {
			if(val[i] > max){
				max = val[i];
			}
		}
		if(max == 0){
			return val;
		}
		for (int i = 0; i < normed.length; i++) {
			normed[i] = val[i] / max;
		}
		return normed;
	}
	/**
	 * @return the minVelocity
	 */
	public double[] getMinVelocity() {
		return this.minVelocity;
	}
	/**
	 * @param minVelocity the minVelocity to set
	 */
	public void setMinVelocity(double[] minVelocity) {
		this.minVelocity = minVelocity;
	}
	/**
	 * @return the maxVelocity
	 */
	public double[] getMaxVelocity() {
		return this.maxVelocity;
	}
	/**
	 * @param maxVelocity the maxVelocity to set
	 */
	public void setMaxVelocity(double[] maxVelocity) {
		this.maxVelocity = maxVelocity;
	}
	/**
	 * @return the meanVelocity
	 */
	public double[] getMeanVelocity() {
		return this.meanVelocity;
	}
	/**
	 * @param meanVelocity the meanVelocity to set
	 */
	public void setMeanVelocity(double[] meanVelocity) {
		this.meanVelocity = meanVelocity;
	}
	/**
	 * @return the minAcceleration
	 */
	public double[] getMinAcceleration() {
		return this.minAcceleration;
	}
	/**
	 * @param minAcceleration the minAcceleration to set
	 */
	public void setMinAcceleration(double[] minAcceleration) {
		this.minAcceleration = minAcceleration;
	}
	/**
	 * @return the maxAcceleration
	 */
	public double[] getMaxAcceleration() {
		return this.maxAcceleration;
	}
	/**
	 * @param maxAcceleration the maxAcceleration to set
	 */
	public void setMaxAcceleration(double[] maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
	}
	/**
	 * @return the meanAcceleration
	 */
	public double[] getMeanAcceleration() {
		return this.meanAcceleration;
	}
	/**
	 * @param meanAcceleration the meanAcceleration to set
	 */
	public void setMeanAcceleration(double[] meanAcceleration) {
		this.meanAcceleration = meanAcceleration;
	}
	/**
	 * @return the movedDistance
	 */
	public double[] getMovedDistance() {
		return this.movedDistance;
	}
	/**
	 * @param movedDistance the movedDistance to set
	 */
	public void setMovedDistance(double[] movedDistance) {
		this.movedDistance = movedDistance;
	}
	/**
	 * @return the mouseMoveTime
	 */
	public double getMouseMoveTime() {
		return this.mouseMoveTime;
	}
	/**
	 * @param mouseMoveTime the mouseMoveTime to set
	 */
	public void setMouseMoveTime(double mouseMoveTime) {
		this.mouseMoveTime = mouseMoveTime;
	}
	/**
	 * @return the minPauseNClick
	 */
	public double getMinPauseNClick() {
		return this.minPauseNClick;
	}
	/**
	 * @param minPauseNClick the minPauseNClick to set
	 */
	public void setMinPauseNClick(double minPauseNClick) {
		this.minPauseNClick = minPauseNClick;
	}
	/**
	 * @return the maxPauseNClick
	 */
	public double getMaxPauseNClick() {
		return this.maxPauseNClick;
	}
	/**
	 * @param maxPauseNClick the maxPauseNClick to set
	 */
	public void setMaxPauseNClick(double maxPauseNClick) {
		this.maxPauseNClick = maxPauseNClick;
	}
	/**
	 * @return the meanPauseNClick
	 */
	public double getMeanPauseNClick() {
		return this.meanPauseNClick;
	}
	/**
	 * @param meanPauseNClick the meanPauseNClick to set
	 */
	public void setMeanPauseNClick(double meanPauseNClick) {
		this.meanPauseNClick = meanPauseNClick;
	}
	/**
	 * @return the minClickTime
	 */
	public double getMinClickTime() {
		return this.minClickTime;
	}
	/**
	 * @param minClickTime the minClickTime to set
	 */
	public void setMinClickTime(double minClickTime) {
		this.minClickTime = minClickTime;
	}
	/**
	 * @return the maxClickTime
	 */
	public double getMaxClickTime() {
		return this.maxClickTime;
	}
	/**
	 * @param maxClickTime the maxClickTime to set
	 */
	public void setMaxClickTime(double maxClickTime) {
		this.maxClickTime = maxClickTime;
	}
	/**
	 * @return the meanClickTime
	 */
	public double getMeanClickTime() {
		return this.meanClickTime;
	}
	/**
	 * @param meanClickTime the meanClickTime to set
	 */
	public void setMeanClickTime(double meanClickTime) {
		this.meanClickTime = meanClickTime;
	}
	
	
	

}
