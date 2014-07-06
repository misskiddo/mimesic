package com.oe.sdk.model;

import java.io.Serializable;

/**
 * The abstract class <code>PhoneNumber<code> provides an
 * abstract <code>isValid()</code> method to validate the
 * phone number.
 * 
 * @author Michele Carotta
 *
 */
public abstract class PhoneNumber implements Serializable {
	private static final long serialVersionUID = 7952756158244547482L;

	protected String number;	// Phone number.

	public PhoneNumber(final String number) {
		this.number = number;
	}

	public abstract boolean isValid();

	public String getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return getNumber();
	}

}
