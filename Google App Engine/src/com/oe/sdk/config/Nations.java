package com.oe.sdk.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.oe.sdk.model.Nation;
import com.oe.sdk.util.Str;
/**
 * <p>The <code>Nations</code> class offers the reliability of his parent class
 * to provide the management relative to the international sending.</p>
 * 
 * <p>It's concerned with a set <code>DEF_NATION</code> of default nations
 * in which the sending is allowed. Anyway, these nations are taken into considerations
 * only when no JAR of properties file are found.</p>
 * @author Michele Carotta
 *
 */
public final class Nations extends PropertiesLoader {
	public static final Nation UNKNOWN_NATION = new Nation("??","??","??");
	public static final Nation NO_NATION = new Nation("**","**","**");
	private static final String NATIONS_FILENAME = "/nations.properties";

	@SuppressWarnings("serial")
	private static final Properties DEF_NATION = new Properties() {{
		setProperty("it_IT", "+39");
		setProperty("es_ES", "+34");
	}};

	private List<Nation> nations;

	// implements the singleton pattern
	private static Nations _instance;
	public static synchronized Nations getInstance() {
		if (_instance == null) {
			_instance = new Nations();
			if(!_instance.load()) {
				_instance = null; // this never happens 'cause we have default nations!
			}
		}
		return _instance;
	}

	/**
	 * The function checks that all the values in the properties file are not empty.
	 * While checks the properties, instances the array of <code>Nation</code>.
	 * 
	 * @return the key inside of which the error occurs, otherwise null.
	 * @see com.oe.sdk.config.PropertiesLoader#checkProperties()
	 */
	@Override
	protected String checkProperties() {
		String iso639_iso3166,prefix;
		if (!properties.isEmpty()) {	// let's check each key is in the correct format (i.e., iso639_iso3166)
			nations = new ArrayList<Nation>(properties.size());
			for (final Map.Entry<? extends Object,? extends Object> entry : properties.entrySet()) {
				iso639_iso3166 = entry.getKey().toString();
				prefix = entry.getValue().toString();
				if(Str.isEmpty(prefix))
					return iso639_iso3166;
				if (!iso639_iso3166.matches("[a-zA-Z]{2}_[a-zA-Z]{2}"))
					return iso639_iso3166;
				nations.add(new Nation(iso639_iso3166.substring(0,2),iso639_iso3166.substring(3),prefix));
			}
		}
		return null;
	}

	/**
	 * The function verifies if there is a <code>Nation</code>
	 * in which the <code>iso911</code> attribute is equals to the string passed as argument.
	 * In this case the relative <code>Nation</code> is returned, otherwise UNKNOWN_NATION.
	 * 
	 * @param iso911 the string of the nation in <code>ISO911</code> standard.
	 * @return the relative instance of <code>Nation</code>, null otherwise.
	 */
	public Nation getByISO3166(final String iso3166) {
		for (final Nation nation : nations) {
			if (nation.equals(iso3166))
				return nation;
		}
		return UNKNOWN_NATION;
	}

	public Nation getPhoneNumberNation(final String phone_number) {
		if (Str.isEmpty(phone_number))
			return UNKNOWN_NATION;
		for (final Nation nation : nations) {
			if (phone_number.startsWith(nation.getPrefix()))
				return nation;
		}
		return UNKNOWN_NATION;
	}

	public List<Nation> getNations() {
		return Collections.unmodifiableList(nations);
	}

	@Override
	public String toString() {
		final StringBuilder toPrint = new StringBuilder();
		int i = 0;
		for(final Nation nation : nations) {
			if (i++>0) {
				toPrint.append(',');
			}
			toPrint.append(nation);
		}
		return toPrint.toString();
	}

	@Override
	protected Properties getDefaultProperties() {
		return DEF_NATION;
	}
	@Override
	protected String getFileName() {
		return NATIONS_FILENAME;
	}

}

