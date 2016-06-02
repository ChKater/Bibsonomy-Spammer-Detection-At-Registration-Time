package de.luh.chkater.spammerdetection.keyboard;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.Pair;


/**
 * DVORAK Keyboard Layout
 *
 * @author Christian Kater
 */
public class DVORAK extends KeyBoardLayout{

	@Override
	public Map<Character, Integer> keyToFinger() {
		Map<Character, Integer> keyToFinger = new HashMap<>();
		String littleFingerLeft ="`~1!2@\"'A:;";
		add(littleFingerLeft, LITTLE_FINGER_LEFT, keyToFinger);
		String ringFingerLeft = "<,OQ3#";
		add(ringFingerLeft, RING_FINGER_LEFT, keyToFinger);
		String middleFingerLeft ="4$>.EJ";
		add(middleFingerLeft, MIDDLE_FINGER_LEFT, keyToFinger);
		String forefingerLeft = "5%6^PUKYIX";
		add(forefingerLeft, FOREFINGERFINGER_LEFT, keyToFinger);
		String forefingerRight = "7&FDB8*GHM";
		add(forefingerRight, FOREFINGERFINGER_RIGHT, keyToFinger);
		String middleFingerRight = "9(CTW";
		add(middleFingerRight, MIDDLE_FINGER_RIGHT, keyToFinger);
		String ringFingerRight = "0)RNV";
		add(ringFingerRight, RING_FINGER_RIGHT, keyToFinger);
		String littleFingerRight = "{}[]LSZ-_/?+=|\\";
		add(littleFingerRight, LITTLE_FINGER_RIGHT, keyToFinger);
		return keyToFinger;
	}

	@Override
	public Map<Character, Pair<Double, Double>> keyToPosition() {
		Map<Character, Pair<Double, Double>> keyToPosition = new HashMap<>();
		String firstRow = "~1234567890[]";
		addPosition(0, 0, firstRow, keyToPosition);
		String firstRowSpeacial = "!@#$%^&*(){}";
		addPosition(0, 1, firstRowSpeacial, keyToPosition);
		String secondRow = "\"<>PYFGCRL?+|";
		addPosition(1, 1.66, secondRow, keyToPosition);
		String secondRowSpecial = "',.";
		addPosition(1, 1.66, secondRowSpecial, keyToPosition);
		String secondRowSpecial1 = "/=\\";
		addPosition(1, 11.66, secondRowSpecial1, keyToPosition);
		String thirdRow = "AOEUIDHTNS-;'";
		addPosition(2, 2, thirdRow, keyToPosition);
		String thirdRowSpeacial = "_";
		addPosition(2, 12, thirdRowSpeacial, keyToPosition);
		String fourthRow = ":QJKXBMWVZ";
		addPosition(3, 2.33, fourthRow, keyToPosition);
		String fourthRowSpeacial = ";";
		addPosition(3, 2.33, fourthRowSpeacial, keyToPosition);
		return keyToPosition;
	}

}
