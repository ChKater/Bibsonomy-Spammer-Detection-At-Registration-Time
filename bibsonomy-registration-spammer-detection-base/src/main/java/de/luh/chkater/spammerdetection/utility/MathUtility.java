package de.luh.chkater.spammerdetection.utility;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class MathUtility {

	
	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return todo
	 */
	public static double getDirectionAngle(double x1, double y1, double x2, double y2){
		double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 -y1));
		if(angle < 0){
			return angle + 360.0;
		}
		return angle;
	}
	
	public static double distance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
}
