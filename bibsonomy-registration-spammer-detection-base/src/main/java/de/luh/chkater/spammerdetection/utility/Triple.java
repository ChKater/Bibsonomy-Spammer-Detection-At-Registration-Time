package de.luh.chkater.spammerdetection.utility;

/**
 * Triple consist of thre objects
 *
 * @author kater
 */
public class Triple<X, Y, Z> {

	private X first;
	private Y second;
	private Z third;

	/**
	 * 
	 */
	public Triple() {
		super();
	}

	/**
	 * @param first
	 * @param second
	 */
	public Triple(X first, Y second, Z third) {
		super();
		this.first = first;
		this.second = second;
		this.third = third;
	}

	/**
	 * @return the first
	 */
	public X getFirst() {
		return this.first;
	}

	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(X first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public Y getSecond() {
		return this.second;
	}

	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(Y second) {
		this.second = second;
	}

	/**
	 * @return the third
	 */
	public Z getThird() {
		return this.third;
	}

	/**
	 * @param third
	 *            the third to set
	 */
	public void setThird(Z third) {
		this.third = third;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triple other = (Triple) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}

}
