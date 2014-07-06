package com.oe.sdk.model;

/**
 * The enum contains the possible values for the types of credit available.
 * Values specified at constructor are abbreviations codes for the credit types.
 * @author Michele Carotta
 *
 */
public enum CreditType {
	GOLD ("GS"),
	GOLD_PLUS ("GP"),
	SILVER ("SI"),
	TITANIUM ("TI"),
	NUMBER_LOOKUP ("NL"),
	OTHER ("EE");

	private String code;

	private CreditType(final String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static CreditType fromCode(final String code) {
		for (final CreditType credit_type : CreditType.values()) {
			if (credit_type.code.equalsIgnoreCase(code))
				return credit_type;
		}
		throw new IllegalArgumentException("Invalid credit type: "+code);
	}
}
