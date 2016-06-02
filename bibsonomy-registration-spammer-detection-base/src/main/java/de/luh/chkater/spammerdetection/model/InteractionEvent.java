package de.luh.chkater.spammerdetection.model;

import java.util.List;

/**
 * Model class for an interaction event
 *
 * @author kater
 */
public class InteractionEvent {

	private String type;
	private String target;
	private int time;
	private List<Integer> data;

	/**
	 * 
	 */
	public InteractionEvent() {
		super();
	}

	/**
	 * @param type
	 * @param target
	 * @param timeDiff
	 * @param data
	 */
	public InteractionEvent(String type, String target, int timeDiff, List<Integer> data) {
		super();
		this.type = type;
		this.target = target;
		this.time = timeDiff;
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the timeDiff
	 */
	public int getTime() {
		return this.time;
	}

	/**
	 * @param timeDiff
	 *            the timeDiff to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the data
	 */
	public List<Integer> getData() {
		return this.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<Integer> data) {
		this.data = data;
	}

}
