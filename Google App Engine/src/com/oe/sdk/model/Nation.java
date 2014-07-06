package com.oe.sdk.model;

import java.util.Locale;

import com.oe.sdk.util.Str;

/**
 * The class <code>Nation</code> contains the couple of values that we use to manage
 * the association between nations (standard ISO911) and international prefixes.
 * 
 * @author Michele Carotta
 *
 */
public class Nation {
	private final String iso639;		// Language code in ISO 639 standard.
	private final String iso3166;	// Country code in ISO 3166 standard.
	private final String prefix;		// International phone prefix.

	public Nation(final String iso639, final String iso3166, final String prefix) {
		this.iso639 = iso639.toLowerCase();
		this.iso3166 = iso3166.toUpperCase();
		this.prefix = Str.stripNonNumeric(prefix);
	}

	public String getIso639() {
		return iso639;
	}
	public String getIso3166() {
		return iso3166;
	}
	public String getPrefix() {
		return prefix;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Nation) {
			final Nation tmp_nation = (Nation)obj;
			return tmp_nation.getIso3166().equals(getIso3166());
		}
		if (obj instanceof String) {
			final String iso3166 = (String)obj;
			return iso3166.equalsIgnoreCase(this.iso3166);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		final Locale locale = new Locale(iso639,iso3166);
		return locale.getDisplayCountry();
	}

}
