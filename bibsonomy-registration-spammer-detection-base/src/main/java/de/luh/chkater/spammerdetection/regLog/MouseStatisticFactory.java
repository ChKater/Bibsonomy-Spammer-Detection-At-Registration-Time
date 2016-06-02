package de.luh.chkater.spammerdetection.regLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.User;

import com.ibm.icu.impl.UBiDiProps;

import de.luh.chkater.spammerdetection.model.InteractionEvent;
import de.luh.chkater.spammerdetection.model.LoggedInteractionFactory;
import de.luh.chkater.spammerdetection.utility.MathUtility;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class MouseStatisticFactory {

	private static final int TIME_BORDER = 1000;
	private static Map<String, MouseStatistic> cache = new HashMap<>();

	public synchronized static MouseStatistic get(User user, int numberOfDirection) {
		MouseStatistic cached = cache.get(user.getName());
		if(user.getRegistrationLog() == null){
			return null;
		}
		if (cached == null) {
			List<List<InteractionEvent>> VelAccEvents = LoggedInteractionFactory.getEvents(user,
					Arrays.asList("mousemove"), TIME_BORDER);
			if(VelAccEvents == null){
				return null;
			}

			double[] minVelocity = new double[numberOfDirection];
			double[] maxVelocity = new double[numberOfDirection];
			double[] meanVelocity = new double[numberOfDirection];
			double[] prevVelocity = new double[numberOfDirection];
			double[] minAcceleration = new double[numberOfDirection];
			double[] maxAcceleration = new double[numberOfDirection];
			double[] meanAcceleration = new double[numberOfDirection];
			double[] occourence = new double[numberOfDirection];
			double directionSize = 360.0 / numberOfDirection;

			double mouseMoveTime = 0;

			for (int i = 0; i < numberOfDirection; i++) {
				prevVelocity[i] = -1.0;
				minVelocity[i] = Double.MAX_VALUE;
				maxVelocity[i] = Double.MIN_VALUE;
				minAcceleration[i] = Double.MAX_VALUE;
				maxAcceleration[i] = Double.MIN_VALUE;
			}

			Integer time = LoggedInteractionFactory.getTotalTime(user);
			if(time == null){
				return null;
			}
			double totalTime = time;
			
			

			for (List<InteractionEvent> events : VelAccEvents) {
				InteractionEvent prev = events.get(0);
				for (int i = 1; i < events.size(); i++) {
					InteractionEvent cur = events.get(i);

					Integer x1 = prev.getData().get(0);
					Integer y1 = prev.getData().get(1);
					Integer x2 = cur.getData().get(0);
					Integer y2 = cur.getData().get(1);

					double distance = MathUtility.distance(x1, y1, x2, y2);
					double angle = MathUtility.getDirectionAngle(x1, y1, x2, y2);
					double timeDiff = cur.getTime() - prev.getTime();
					int pos = (int) (angle / directionSize);

					occourence[pos] = occourence[pos] + 1;

					// velocity
					double velocity = Math.abs(distance / timeDiff);
					if (velocity < minVelocity[pos]) {
						minVelocity[pos] = velocity;
					}
					if (velocity > maxVelocity[pos]) {
						maxVelocity[pos] = velocity;
					}
					meanVelocity[pos] += velocity;

					// acceleration
					if (prevVelocity[pos] >= 0) {
						double acceleration = Math.abs((velocity - prevVelocity[pos]) / timeDiff);
						if (acceleration < minAcceleration[pos]) {
							minAcceleration[pos] = velocity;
						}
						if (acceleration > maxAcceleration[pos]) {
							maxAcceleration[pos] = velocity;
						}
						meanAcceleration[pos] += acceleration;
					}

					prevVelocity[pos] = velocity;

					mouseMoveTime += timeDiff;
					prev = cur;
				}
			}
			mouseMoveTime /= totalTime;	

			for (int i = 0; i < numberOfDirection; i++) {
				meanVelocity[i] = meanVelocity[i] / occourence[i];
				meanAcceleration[i] = meanAcceleration[i] / (occourence[i] - VelAccEvents.size());

			}

			List<List<InteractionEvent>> moveEvents = LoggedInteractionFactory.getEvents(user,
					Arrays.asList("mousemove"), Integer.MAX_VALUE);
			if(moveEvents == null){
				return null;
			}

			double[] movedDistance = new double[numberOfDirection];

			double totalDistance = 0;

			for (List<InteractionEvent> events : moveEvents) {
				InteractionEvent prev = events.get(0);
				for (int i = 1; i < events.size(); i++) {
					InteractionEvent cur = events.get(i);

					Integer x1 = prev.getData().get(0);
					Integer y1 = prev.getData().get(1);
					Integer x2 = cur.getData().get(0);
					Integer y2 = cur.getData().get(1);

					double distance = MathUtility.distance(x1, y1, x2, y2);
					double angle = MathUtility.getDirectionAngle(x1, y1, x2, y2);
					int pos = (int) (angle / directionSize);

					totalDistance += distance;
					// Direction Distribution
					movedDistance[pos] = movedDistance[pos] + distance;
					prev = cur;
					
					
				}
			}
			for (int i = 0; i < numberOfDirection; i++) {
				movedDistance[i] = movedDistance[i] / totalDistance;
			}

			List<List<InteractionEvent>> moveClickEvents = LoggedInteractionFactory.getEvents(user,
					Arrays.asList("mousemove", "mousedown"), Integer.MAX_VALUE);
			if(moveClickEvents == null){
				return null;
			}
			double minPauseNClick = Double.MAX_VALUE;
			double maxPauseNClick = Double.MIN_VALUE;
			double meanPauseNClick = 0;
			int pausNClickOcc = 0;

			for (List<InteractionEvent> events : moveClickEvents) {
				InteractionEvent prev = events.get(0);
				for (int i = 1; i < events.size(); i++) {
					InteractionEvent cur = events.get(i);

					double timeDiff = cur.getTime() - prev.getTime();

					if (prev.getType().equals("mousemove") && cur.getType().equals("mousedown")) {
						pausNClickOcc++;

						// Pause and click
						double pauseNClick = Math.abs(timeDiff);
						if (pauseNClick < minPauseNClick) {
							minPauseNClick = pauseNClick;
						}
						if (pauseNClick > maxPauseNClick) {
							maxPauseNClick = pauseNClick;
						}
						meanPauseNClick += pauseNClick;

					}
					prev = cur;
				}
			}

			meanPauseNClick /= pausNClickOcc;
			
			List<List<InteractionEvent>> mouseUpDownEvents = LoggedInteractionFactory.getEvents(user,
					Arrays.asList("mouseup", "mousedown"), Integer.MAX_VALUE);

			if(mouseUpDownEvents == null){
				return null;
			}
			
			double minClickTime = Double.MAX_VALUE;
			double maxClickTime = Double.MIN_VALUE;
			double meanClickTime = 0;

			int clickTravelOcc = 0;

			for (List<InteractionEvent> events : mouseUpDownEvents) {
				InteractionEvent prev = events.get(0);
				for (int i = 1; i < events.size(); i++) {
					InteractionEvent cur = events.get(i);

					double timeDiff = cur.getTime() - prev.getTime();

					if (prev.getType().equals("mousedown") && cur.getType().equals("mouseup")) {
						clickTravelOcc++;

						// Pause and click
						double clickTime = Math.abs(timeDiff);
						if(Double.isInfinite(clickTime)){
							System.out.println(clickTime);
						}
						if (clickTime < minClickTime) {
							minClickTime = clickTime;
						}
						if (clickTime > maxClickTime) {
							maxClickTime = clickTime;
						}
						meanClickTime += clickTime;

					}
					prev = cur;
				}
			}

			meanClickTime /= clickTravelOcc;
			cached = new MouseStatistic(minVelocity, maxVelocity, meanVelocity, minAcceleration, maxAcceleration,
					meanAcceleration, movedDistance, mouseMoveTime, minPauseNClick, maxPauseNClick, meanPauseNClick,
					minClickTime, maxClickTime, meanClickTime);
			cache.put(user.getName(), cached);
		}
		return cached;
	}

}
