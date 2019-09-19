package com.capitalone.chariot.nuxeo.enterprise.webapp.voc;

/*
 * {"ordering":2,"obsolete":0,"id":"author","displayLabel":"Author","label":"Author","computedId":"author","absoluteLabel":"Author"}
 */

public class VocabularyItem {

	private String ordering = "1";
	private String obsolete = "0";
	private String id;
	private String computedId;
	private String label;
	private String displayLabel;
	private String absoluteLabel;

	/**
	 * @param id
	 * @param label
	 */
	public VocabularyItem(String id, String label) {
		this.id = id;
		computedId = id;
		this.label = label;
		displayLabel = label;
		absoluteLabel = label;
	}

	/**
	 * @return the ordering
	 */
	public String getOrdering() {
		return ordering;
	}

	/**
	 * @param ordering
	 *            the ordering to set
	 */
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	/**
	 * @return the obsolete
	 */
	public String getObsolete() {
		return obsolete;
	}

	/**
	 * @param obsolete
	 *            the obsolete to set
	 */
	public void setObsolete(String obsolete) {
		this.obsolete = obsolete;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the computedId
	 */
	public String getComputedId() {
		return computedId;
	}

	/**
	 * @param computedId
	 *            the computedId to set
	 */
	public void setComputedId(String computedId) {
		this.computedId = computedId;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the displayLabel
	 */
	public String getDisplayLabel() {
		return displayLabel;
	}

	/**
	 * @param displayLabel
	 *            the displayLabel to set
	 */
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	/**
	 * @return the absoluteLabel
	 */
	public String getAbsoluteLabel() {
		return absoluteLabel;
	}

	/**
	 * @param absoluteLabel
	 *            the absoluteLabel to set
	 */
	public void setAbsoluteLabel(String absoluteLabel) {
		this.absoluteLabel = absoluteLabel;
	}
}
