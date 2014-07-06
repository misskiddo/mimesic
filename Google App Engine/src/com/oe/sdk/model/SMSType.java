package com.oe.sdk.model;
/**
 * <p>The enum <code>SMSType</code> provides
 * to maintain the type of SMS codes.</p>
 * <p>For more informations about the meaning of SMS types,
 * make reference to <a href="http://www.smstrend.net">www.sms-trend.net</a>.</p>
 * 
 * @author Michele Carotta
 *
 */
public enum SMSType {
	GOLD("GS",false),		// 'Gold' SMS type
	GOLD_PLUS("GP",true),	// 'Gold Plus' SMS type
	SILVER("SI",false),		// 'Silver' SMS type
	TITANIUM("TI",true);		// 'Titanium' SMS type

	private String code;
	private boolean has_custom_tpoa;

	private SMSType(final String code, final boolean has_custom_tpoa) {
		this.code = code;
		this.has_custom_tpoa = has_custom_tpoa;
	}

	public String code() {
		return code;
	}

	public boolean hasCustomTPOA() {
		return has_custom_tpoa;
	}

	@Override
	public String toString() {
		return "'" + name() + "' (with value '" + code + "')";
	}

	public static SMSType fromCode(final String code) {
		for (final SMSType credit_type : SMSType.values()) {
			if (credit_type.code.equalsIgnoreCase(code))
				return credit_type;
		}
		throw new IllegalArgumentException("Invalid credit type: "+code);
	}

}
