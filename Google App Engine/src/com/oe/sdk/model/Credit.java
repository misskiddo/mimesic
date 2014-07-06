package com.oe.sdk.model;


/**
 * The class <code>Credit</code> provides for the customer credit.
 * The data members for this class are:<br>
 * <p>
 * - <code>CreditType creditType;</code><br>
 * - <code>Nation nation;</code><br>
 * - <code>int count;</code><br>
 * </p>
 * <code>creditType</code> is an enum type defining the different
 * type of credits contemplated by the system.<br>
 * <code>nation</code> is the class containing the couple of values
 * that is used to manage the association between nations
 * (standard ISO639) and international prefixes.<br>
 * <code>count</code> is the credit counter.
 * 
 * @author Michele Carotta
 *
 */
public class Credit {
	private final CreditType creditType;		// Type of credit.
	private final Nation nation;				// Nation relative to the credit.
	private final int availability;				// Credit counter.

	public Credit(final CreditType credit_type, final Nation nation, final int availability) {
		creditType = credit_type;
		this.nation = nation;
		this.availability = availability;
	}

	public CreditType getCreditType() {
		return creditType;
	}
	public Nation getNation() {
		return nation;
	}
	public int getCount() {
		return availability;
	}

	@Override
	public String toString() {
		final StringBuilder toPrint = new StringBuilder()
		.append("(creditType:").append(creditType).append(",");
		if (nation != null) {
			toPrint.append("nation=").append(nation).append(",");
		}
		toPrint.append("count='").append(availability).append(")");
		return toPrint.toString();
	}

}
