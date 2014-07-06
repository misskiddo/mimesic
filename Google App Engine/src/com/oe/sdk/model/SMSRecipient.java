package com.oe.sdk.model;

import com.oe.sdk.config.Nations;
import com.oe.sdk.exceptions.InvalidRecipientException;
import com.oe.sdk.util.Str;

/**
 * The class <code>SMSRecipient</code> extends the <code>PhoneNumber</code>
 * interface and implements the <code>Serializable</code> interface.
 * It adds to its parent class the method <code>boolean isInternational()</code>
 * that checks if the phone number match semantically with ad international
 * phone number (starting with '00' or '+'), and the method
 * <code>Nation getNation()</code> that return an instance of
 * the <code>Nation</code> class relative to the recipient
 * 
 * @author Michele Carotta
 */
public class SMSRecipient extends PhoneNumber {
	private static final long serialVersionUID = -929895041838826366L;
	private boolean international;

	/**
	 * The constructor add the default international prefix clean the phone
	 * number string from non numeric digits.
	 * @throws InvalidRecipientException
	 */
	public SMSRecipient(final String number) {
		super(number);
		cleanIntl();
	}

	private void cleanIntl()  {
		if (!Str.isEmpty(number)) {
			if (number.charAt(0) == '+') {
				number = number.substring(1);
				international = true;
			} else {
				if (number.startsWith("00")) {
					number = number.substring(2);
					international = true;
				} else {
					international = false;
				}
			}
			number = Str.stripNonNumeric(number);
		}
	}

	/**
	 * Check that the number is a number :)
	 * 
	 * @return true or false
	 */
	@Override
	public boolean isValid() {
		if (Str.isEmpty(number))
			return false;
		return number.length() > 2;
	}

	/**
	 * Check if the phone number begin with '+' or with '00'.
	 * 
	 * @return true or false
	 */
	public boolean isInternational() {
		return international;
	}

	/**
	 * The function get the Nation of recipient.
	 * 
	 * @return an instance of <code>Nation</code> relative to the recipient,
	 * <code>NO_NATION</code> if the recipient hasn't an international prefix,
	 * <code>UNKNOWN_NATION</code> if the prefix is unknown, <code>null</code>
	 * if the object <code>Recipient</code> isn't a valid recipient.
	 */
	public Nation getNation() {
		if (!isValid())
			return Nations.UNKNOWN_NATION;
		if (!isInternational())
			return Nations.NO_NATION;
		return Nations.getInstance().getPhoneNumberNation(number);
	}

	@Override
	public String getNumber() {
		return isInternational() ? "+"+number : number;
	}

	@Override
	public String toString() {
		return getNumber();
	}

}
