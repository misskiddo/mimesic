package com.oe.sdk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DDate {
	public static final SimpleDateFormat STANDARD_FORMAT=new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat STANDARD_TIME_FORMAT=new SimpleDateFormat("HH:mm");

	public static String format(final Date date) {
		if (date==null)
			return Str.EMPTY_STRING;
		return STANDARD_FORMAT.format(date);
	}

	/**
	 * Converts a <code>String</code> to a <code>Date</code> object,
	 * if the <code>String</code> is empty return <code>null</code>.
	 * @param date the <code>String</code> to convert
	 * @return the instanced <code>Date</code> from the passed <code>String</code>,
	 * <code>null</code> otherwise.
	 */
	public static Date parse(final String date) {
		if (Str.isEmpty(date))
			return null;
		return _parse(date, STANDARD_FORMAT);
	}

	private static Date _parse(final String date, final SimpleDateFormat sdf) {
		try {
			return sdf.parse(date);
		} catch (final ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}

}
