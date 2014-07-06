package com.oe.sdk.util;


public class Str {
	public static final String EMPTY_STRING = "";

	public static boolean isEmpty(final String str) {
		return nullify(str)==null;
	}

	public static String stripNonNumeric(final String toclean) {
		if (isEmpty(toclean))
			return toclean;
		return toclean.replaceAll("[^0-9]", EMPTY_STRING);
	}

	public static String sep() {
		return System.getProperty("file.separator");
	}

	/**<p>
	 * The message origin, also known as TPOA (Transmission Path Originating Address)
	 * is the SMS header field that contains the message sender's number.
	 * ValueSMS can alter this field to include reply path information
	 * (the user's own phone number) or other branding information,
	 * such as the company name.
	 * </p>
	 * <p>
	 * The TPOA field is limited by GSM standards to:<br>
	 * - maximum 16 digits if the origin is numeric (e.g. a phone number), or<br>
	 * - maximum 11 alphabet characters and digits if the origin is alphanumeric (e.g. a company name).
	 * </p>
	 * 
	 */
	public static boolean isValidTPOA(final String tpoa) {
		return tpoa.matches("00[0-9]{7,16}")
		|| tpoa.matches("\\Q+\\E[0-9]{7,16}")
		|| (tpoa.length()<12);
	}

	public static int sms_length(final String sms) {
		int smslen = 0;
		for (int i=0;i<sms.length();i++) {
			switch (sms.charAt(i)) {
			case '\u000C':
			case '\u005E':
			case '\u007B':
			case '\u007D':
			case '\134':
			case '\u005B':
			case '\u007E':
			case '\u005D':
			case '\u007C':
			case '\u20AC': smslen++;
			default: smslen++;
			}
		}
		return smslen;
	}

	private static String nullify(final String str) {
		if (str == null)
			return null;
		if (str.trim().equals(EMPTY_STRING))
			return null;
		return str;
	}

	public static String join(char separator, String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings)
		{
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(s);
		}
		return sb.toString();
	}
}
