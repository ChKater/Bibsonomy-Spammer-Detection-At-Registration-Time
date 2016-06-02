package de.luh.chkater.spammerdetection.keyboard;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.Pair;

/**
 * German Qwertz layout
 *
 * @author kater
 */
public class QwertzDe extends KeyBoardLayout {

	@Override
	public Map<Character, Integer> keyToFinger() {
		Map<Character, Integer> keyToFinger = new HashMap<>();
		String littleFingerLeft = "^°1!2\"QAY@";
		add(littleFingerLeft, LITTLE_FINGER_LEFT, keyToFinger);
		String ringFingerLeft = "3§WSX";
		add(ringFingerLeft, RING_FINGER_LEFT, keyToFinger);
		String middleFingerLeft = "4$EDC";
		add(middleFingerLeft, MIDDLE_FINGER_LEFT, keyToFinger);
		String forefingerLeft = "5%6&RFVTGB";
		add(forefingerLeft, FOREFINGERFINGER_LEFT, keyToFinger);
		String forefingerRight = "7/{8([ZHNUJM";
		add(forefingerRight, FOREFINGERFINGER_RIGHT, keyToFinger);
		String middleFingerRight = "9)]IK;,";
		add(middleFingerRight, MIDDLE_FINGER_RIGHT, keyToFinger);
		String ringFingerRight = "0=}OL:.";
		add(ringFingerRight, RING_FINGER_RIGHT, keyToFinger);
		String littleFingerRight = "?ß\\PÖÄÜ*+~#'-_";
		add(littleFingerRight, LITTLE_FINGER_RIGHT, keyToFinger);
		return keyToFinger;
	}

	@Override
	public Map<Character, Pair<Double, Double>> keyToPosition() {
		Map<Character, Pair<Double, Double>> keyToPosition = new HashMap<>();
		String firstRow = "^1234567890ß´";
		addPosition(0, 0, firstRow, keyToPosition);
		String firstRowSpeacial = "°!\"§$%&()=?`";
		addPosition(0, 0, firstRowSpeacial, keyToPosition);
		String firstRowSpeacial2 = "{[]}\\";
		addPosition(0, 7, firstRowSpeacial2, keyToPosition);
		String secondRow = "QWERTZUIOPÜ+";
		addPosition(1, 1.66, secondRow, keyToPosition);
		String secondRowSpecial = "@";
		addPosition(1, 1.66, secondRowSpecial, keyToPosition);
		String secondRowSpecial2 = "*";
		addPosition(1, 12.66, secondRowSpecial2, keyToPosition);
		String thirdRow = "ASDFGHJKLÖÄ#";
		addPosition(2, 2, thirdRow, keyToPosition);
		String thirdRowSpeacial = "'";
		addPosition(2, 13, thirdRowSpeacial, keyToPosition);
		String fourthRow = "YXCVBNM;:-";
		addPosition(3, 2.33, fourthRow, keyToPosition);
		String fourthRowSpeacial = ",._";
		addPosition(3, 9.33, fourthRowSpeacial, keyToPosition);
		return keyToPosition;
	}

}
