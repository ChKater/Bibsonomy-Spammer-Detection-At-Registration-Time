package de.luh.chkater.spammerdetection.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.User;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.luh.chkater.spammerdetection.utility.Triple;

// TODO: Auto-generated Javadoc
/**
 * Factory for logged regsitrations. Reads the rigistration log of an user and transforms it into a list of events
 *
 * @author kater
 */
public class LoggedInteractionFactory {

	/** The Constant ALL_TYPES. */
	public static final List<String> ALL_TYPES = Arrays.asList("blur", "focus", "focusin", "focusout", "load",
			"resize", "scroll", "unload", "click", "dblclick", "mousedown", "mouseup", "mousemove", "mouseover",
			"mouseout", "mouseenter", "mouseleave", "change", "select", "submit", "keydown", "keypress", "keyup",
			"error");

	/** The cache. */
	private static Map<Triple<String, List<String>, Integer>, List<List<InteractionEvent>>> cache = new HashMap<>();
	
	/** The json object cache. */
	private static Map<String, JsonObject> jsonObjectCache = new HashMap<>();

	
	/**
	 * generate a list of certain types of events from the registration log. 
	 *
	 * @param user the user
	 * @param types the eventtypes to select
	 * @param timeBorder the time border
	 * @return the events
	 */
	public synchronized static List<List<InteractionEvent>> getEvents(User user, List<String> types, int timeBorder) {
		Triple<String, List<String>, Integer> key = new Triple<String, List<String>, Integer>(user.getName(), types,
				timeBorder);
		if(user.getRegistrationLog() == null) {
			return null;
		}
		List<List<InteractionEvent>> cached = cache.get(key);
		if (cached == null) {
			List<InteractionEvent> events = new LinkedList<>();
			JsonObject regLog = getJsonobject(user);
			if(regLog == null){
				return null;
			}
			for (String type : types) {
				String typeNumber = getTypeNumber(regLog, type);
				if (typeNumber == null) {
					continue;
				}
				Set<Entry<String, JsonElement>> targets = regLog.get("loggedInteractions").getAsJsonObject()
						.get(typeNumber).getAsJsonObject().entrySet();
				for (Entry<String, JsonElement> target : targets) {
					String targetName = getTypeName(regLog, target.getKey());
					JsonArray values = target.getValue().getAsJsonArray();
					int prevTime = 0;
					JsonArray prev;
					List<Integer> prevData = null;
					for (JsonElement jsonElement : values) {
						JsonArray value = jsonElement.getAsJsonArray();
						int timediff = value.get(0).getAsInt();
						if (timediff < 0) {
							timediff = 0;
						}
						int time = prevTime + timediff;
						prevTime = time;
						
						List<Integer> data = new ArrayList<>(value.size() - 1);
						if(prevData == null){
							prevData = new ArrayList<>();
							for (int i = 0; i < value.size(); i++) {
								prevData.add(0);
							}
						}
						int end;
						if(type.startsWith("select") || type.startsWith("key")){
							end = 1;
						} else {
							end = data.size();
						}
						
						boolean skip = false;

						for (int i = 1; i < value.size(); i++) {
							int curValue = 0;
							try {
								curValue = value.get(i).getAsInt();
							} catch (Exception e) {
								skip = true;
								break;
							}
							if (i < end) {
								Integer prevValue = prevData.get(i);
								data.add(curValue + prevValue);
								prevData.set(i, prevValue + curValue);
							} else {
								data.add(curValue);
							}
						}
						if(skip){
							continue;
						}
						events.add(new InteractionEvent(type, targetName, time, data));
						prev = value;

					}
				}

			}
			Collections.sort(events, new Comparator<InteractionEvent>() {

				@Override
				public int compare(InteractionEvent o1, InteractionEvent o2) {
					return Integer.compare(o1.getTime(), o2.getTime());
				}
			});
			cached = new LinkedList<>();

			List<InteractionEvent> currentList = new LinkedList<>();
			InteractionEvent prev = null;
			for (InteractionEvent cur : events) {
				if (prev != null) {
					int timeDiff = cur.getTime() - prev.getTime();
					if (timeDiff > timeBorder) {
						cached.add(currentList);
						currentList.clear();
					}
				}
				currentList.add(cur);
				prev = cur;

			}
			if (currentList.size() > 0) {
				cached.add(currentList);
			}
			cache.put(key, cached);
		}
		return cached;
	}

	/**
	 * Gets the total time.
	 *
	 * @param user the user
	 * @return the total time
	 */
	public static Integer getTotalTime(User user) {
		List<List<InteractionEvent>> events = getEvents(user, ALL_TYPES, Integer.MAX_VALUE);
		if(events == null){
			return null;
		}
		List<InteractionEvent> allEvents = events.get(0);
		if(allEvents == null){
			return null;
		}
		return allEvents.get(allEvents.size() - 1).getTime();
	}

	/**
	 * Gets the type number.
	 *
	 * @param regLog the reg log
	 * @param type the type
	 * @return the type number
	 */
	private static String getTypeNumber(JsonObject regLog, String type) {
		Set<Entry<String, JsonElement>> assignedNumbers = regLog.get("assignedNumbers").getAsJsonObject().entrySet();
		for (Entry<String, JsonElement> entry : assignedNumbers) {
			if (entry.getKey().equals(type)) {
				return entry.getValue().getAsString();
			}
		}
		return null;
	}

	/**
	 * Gets the type name.
	 *
	 * @param regLog the reg log
	 * @param number the number
	 * @return the type name
	 */
	private static String getTypeName(JsonObject regLog, String number) {
		Set<Entry<String, JsonElement>> assignedNumbers = regLog.get("assignedNumbers").getAsJsonObject().entrySet();
		for (Entry<String, JsonElement> entry : assignedNumbers) {
			if (entry.getValue().getAsString().equals(number)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Gets the jsonobject.
	 *
	 * @param user the user
	 * @return the jsonobject
	 */
	private static JsonObject getJsonobject(User user) {
		JsonObject object = jsonObjectCache.get(user.getName());
		if (object == null) {
			JsonParser parser = new JsonParser();
			try {
				object = parser.parse(user.getRegistrationLog()).getAsJsonObject();
			} catch (JsonSyntaxException e) {
				return null;
			}
		}
		return object;

	}
}
