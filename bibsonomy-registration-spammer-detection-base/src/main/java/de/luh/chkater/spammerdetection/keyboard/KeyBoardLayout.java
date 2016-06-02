package de.luh.chkater.spammerdetection.keyboard;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.Pair;

/**
 * Keyboard layout definition
 *
 * @author Christian Kater
 */
public abstract class KeyBoardLayout {

	public static final int FOREFINGERFINGER_LEFT = 4;
	public static final int FOREFINGERFINGER_RIGHT = 5;
	public static final int LEFT_HAND = 101;
	public static final int LITTLE_FINGER_LEFT = 1;
	public static final int LITTLE_FINGER_RIGHT = 8;
	public static final int MIDDLE_FINGER_LEFT = 3;
	public static final int MIDDLE_FINGER_RIGHT = 6;
	public static final int RIGHT_HAND = 102;
	public static final int RING_FINGER_LEFT = 2;
	public static final int RING_FINGER_RIGHT = 7;
	public static final int FIRST_ROW = 201;
	public static final int SECOND_ROW = 202;
	public static final int THIRD_ROW = 203;
	public static final int FOURTH_ROW = 204;

	protected void add(String row, int value, Map<Character, Integer> map) {
		for (int i = 0; i < row.length(); i++) {
			map.put(row.charAt(i), value);
		}
	}

	protected void addPosition(double row, double startCol, String toAdd, Map<Character, Pair<Double, Double>> map) {
		for (int i = 0; i < toAdd.length(); i++) {
			map.put(toAdd.charAt(i), new Pair<>(row, i + startCol));
		}
	}

	/**
	 * Maps an char to the corresponding hand in the 10 fingersystem on the
	 * layout
	 * 
	 * @return char to hand map
	 */
	public Map<Character, Integer> keyToHand() {
		Map<Character, Integer> keyToHand = new HashMap<>();
		Map<Character, Integer> keytoFinger = keyToFinger();
		for (Character key : keytoFinger.keySet()) {
			Integer finger = keytoFinger.get(key);
			if (finger == LITTLE_FINGER_LEFT || finger == RING_FINGER_LEFT || finger == MIDDLE_FINGER_LEFT
					|| finger == FOREFINGERFINGER_LEFT) {
				keyToHand.put(key, LEFT_HAND);
			} else {
				keyToHand.put(key, RIGHT_HAND);
			}
		}
		return keyToHand;
	}

	/**
	 * Maps an char to the corresponding row in the 10 fingersystem on the
	 * layout
	 * 
	 * @return map char to row
	 */
	public Map<Character, Integer> keyToRows() {
		Map<Character, Integer> keyToRows = new HashMap<>();
		Map<Character, Pair<Double, Double>> keyToPosition = keyToPosition();
		for (Character key : keyToPosition.keySet()) {
			int value = keyToPosition.get(key).getFirst().intValue();
			int row;
			switch (value) {
			case 0:
				row = FIRST_ROW;
				break;
			case 1:
				row = SECOND_ROW;
				break;
			case 2:
				row = THIRD_ROW;
				break;
			case 3:
				row = FOURTH_ROW;
				break;
			default:
				row = -1;
				break;
			}
			keyToRows.put(key, row);

		}
		return keyToRows;
	}

	/**
	 * Maps an char to the corresponding finger in the 10 fingersystem on the
	 * layout
	 * 
	 * @return map char to finger
	 */
	public abstract Map<Character, Integer> keyToFinger();

	/**
	 * Maps an char to the corresponding position on the layout
	 * @return map char to position
	 */
	public abstract Map<Character, Pair<Double, Double>> keyToPosition();
}
