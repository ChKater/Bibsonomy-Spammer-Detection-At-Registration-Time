package de.luh.chkater.spammerdetection.regLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.User;

import de.luh.chkater.spammerdetection.model.InteractionEvent;
import de.luh.chkater.spammerdetection.model.LoggedInteractionFactory;

/**
 * Creates the Keyboard statistic from the registration log.
 *
 * @author kater
 */
public class KeyboardStatisticFactory {
	private static final int TIME_BORDER = 1000;
	private static Map<String, KeyboardStatistic> cache = new HashMap<>();

	public synchronized static KeyboardStatistic get(User user){
		KeyboardStatistic cached = cache.get(user.getName());
		if(cached == null){
			
			double minDwellTime = Double.MAX_VALUE;
			double maxDwellTime = Double.MIN_NORMAL;
			double meanDwellTime = 0;
			int dwellOcc = 0;
			
			double minFlightTime = Double.MAX_VALUE;
			double maxFlightTime= Double.MIN_NORMAL;
			double meanFlightTime = 0;
			int flightOcc = 0;
			
			List<List<InteractionEvent>> keyboardEvents = LoggedInteractionFactory.getEvents(user,
					Arrays.asList("keyup", "keydown"), TIME_BORDER);
			if(keyboardEvents == null){
				 return null;
			 }
			
			for (List<InteractionEvent> events : keyboardEvents) {
				InteractionEvent prev = events.get(0);
				for (int i = 1; i < events.size(); i++) {
					InteractionEvent cur = events.get(i);
					
					
					double timeDiff = cur.getTime() - prev.getTime();
					
					if(cur.getType().equals("keyup") && prev.getType().equals("keydown")){
						dwellOcc++;
						if(timeDiff < minDwellTime){
							minDwellTime = timeDiff;
						}
						if(timeDiff > maxDwellTime){
							maxDwellTime = timeDiff;
						}
						meanDwellTime+= timeDiff;
					} else if(cur.getType().equals("keydown") && prev.getType().equals("keyup")){
						flightOcc++;
						if(timeDiff < minFlightTime){
							minFlightTime = timeDiff;
						}
						if(timeDiff > maxFlightTime){
							maxFlightTime = timeDiff;
						}
						meanFlightTime+= timeDiff;
					}
					prev = cur;
				}
			}
			
			meanDwellTime /= dwellOcc;
			meanFlightTime /= flightOcc;
			cached = new KeyboardStatistic(minDwellTime, maxDwellTime, meanDwellTime, minFlightTime, maxFlightTime, meanFlightTime);
			cache.put(user.getName(), cached);
		}
		return cached;
		
		
		
	}
}
