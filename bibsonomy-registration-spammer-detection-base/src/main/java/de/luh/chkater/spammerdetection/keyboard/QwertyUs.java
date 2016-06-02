package de.luh.chkater.spammerdetection.keyboard;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.Pair;


/**
 * 
 * US Qwerty Layout
 *
 * @author Christian Kater
 */
public class QwertyUs extends KeyBoardLayout {

	@Override
	public Map<Character, Integer> keyToFinger() {
		Map<Character, Integer> keyToFinger = new HashMap<>();
		String littleFingerLeft = "1!QAZ";
		add(littleFingerLeft, LITTLE_FINGER_LEFT, keyToFinger);
		String ringFingerLeft = "2@WSX";
		add(ringFingerLeft, RING_FINGER_LEFT, keyToFinger);
		String middleFingerLeft = "3#EDC";
		add(middleFingerLeft, MIDDLE_FINGER_LEFT, keyToFinger);
		String forefingerLeft = "4$RFV5%TGB";
		add(forefingerLeft, FOREFINGERFINGER_LEFT, keyToFinger);
		String forefingerRight = "6^YHN7&UJM";
		add(forefingerRight, FOREFINGERFINGER_RIGHT, keyToFinger);
		String middleFingerRight = "8*IK<";
		add(middleFingerRight, MIDDLE_FINGER_RIGHT, keyToFinger);
		String ringFingerRight = "9(OL>";
		add(ringFingerRight, RING_FINGER_RIGHT, keyToFinger);
		String littleFingerRight = "0-_+=P:;/?,\"}|\\";
		add(littleFingerRight, LITTLE_FINGER_RIGHT, keyToFinger);

		return keyToFinger;
	}

	@Override
	public Map<Character, Pair<Double, Double>> keyToPosition() {
		Map<Character, Pair<Double, Double>> keyToPosition = new HashMap<>();
		String firstRow = "~1234567890_=";
		addPosition(0, 0, firstRow, keyToPosition);
		String firstRowSpeacial = "!@#$%^&*()-+";
		addPosition(0, 1, firstRowSpeacial, keyToPosition);
		String secondRow = "QWERTYUIOP[]|";
		addPosition(1, 1.66, secondRow, keyToPosition);
		String secondRowSpecial = "{}\\";
		addPosition(1, 11.66, secondRowSpecial, keyToPosition);
		String thirdRow = "ASDFGHJKL;'";
		addPosition(2, 2, thirdRow, keyToPosition);
		String thirdRowSpeacial = ":\"";
		addPosition(2, 11, thirdRowSpeacial, keyToPosition);
		String fourthRow = "ZXCVBNM,./";
		addPosition(3, 2.33, fourthRow, keyToPosition);
		String fourthRowSpeacial = "<>?";
		addPosition(3, 9.33, fourthRowSpeacial, keyToPosition);
		return keyToPosition;
	}

	

}
