package com.oe.sdk.model;

import com.oe.sdk.config.Nations;


/**
 * <p>The class <code>LookupResult</code> catches the return
 * of the number lookup call. With the information passed
 * to the constructor, it initializes his fields, among which
 * a <code>Nation</code> object and a <code>SMSRecipient</code>
 * object.</p>
 * <p>If offers two useful methods, <code>boolean isValid()</code>
 * and <code>boolean canSendTo()</code>. The first corresponds to the result
 * of the number lookup call, the second, instead, checks if we are allowed
 * to send to the number (a matching is looked within the <code>Nations</code> object).
 * 
 * @author Michele Carotta
 */
public class LookupResult {
	private boolean valid = false;
	private String order_id;
	private SMSRecipient recipient;
	private Nation nation;
	private String nationName;
	private String operator;

	public LookupResult(final String numLookupID) {
		valid = false;
	}
	public LookupResult(final String order_id, final SMSRecipient recipient, final Nation nation, final String nationName, final String operator) {
		valid = true;
		this.order_id = order_id;
		this.recipient = recipient;
		this.nation = nation;
		this.nationName = nationName;
		this.operator = operator;
	}

	/**
	 * The method verify if the <code>Nation</code> of
	 * the number lookup recipient is one to which we can send.
	 * 
	 * @return <code>true</code> in case of success, <code>false</code> otherwise
	 */
	public boolean canSendTo() {
		return isValid() && !nation.equals(Nations.UNKNOWN_NATION);
	}

	/**
	 * The result of the number lookup call.
	 * 
	 * @return <code>true</code> in case of success, <code>false</code> otherwise
	 */
	public boolean isValid() {
		return valid;
	}

	public String getOrder_id() {
		return order_id;
	}

	public SMSRecipient getRecipient() {
		return recipient;
	}

	public Nation getNation() {
		return nation;
	}

	public String getNationName() {
		return nationName;
	}

	public String getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder()
		.append("(valid=").append(valid)
		.append(",order_id=").append(order_id);
		if (valid) {
			sb.append(",recipient=").append(recipient)
			.append(",nation=").append(nation)
			.append(",nationName=").append(nationName)
			.append(",operator=").append(operator).append(')');
		} else {
			sb.append(')');
		}
		return sb.toString();
	}

}
