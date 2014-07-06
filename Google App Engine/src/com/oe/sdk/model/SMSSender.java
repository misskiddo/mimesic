package com.oe.sdk.model;

import java.io.Serializable;

import com.oe.sdk.util.Str;

/**
 * The class <code>SMSSender</code> extends the <code>PhoneNumber</code>
 * interface and implements the <code>Serializable</code> interface.
 * The method <code>boolean isValid()</code> is overridden using the proper
 * checks to the SMS sender number (length and '00' or '+' at the beginning).
 * 
 * @author Michele Carotta
 */
public class SMSSender extends PhoneNumber implements Serializable {
	private static final long serialVersionUID = -5685968385415545355L;

	public SMSSender(final String tpoa) {
		super(tpoa);
	}

	// Checks length and '00' or '+' at the beginning
	@Override
	public boolean isValid() {
		return (getNumber() != null) && Str.isValidTPOA(getNumber());
	}

}
