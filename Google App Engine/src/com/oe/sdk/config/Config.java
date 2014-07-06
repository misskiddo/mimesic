package com.oe.sdk.config;


/**
 * The class <code>Config</code> provides to load properly the configuration file.
 * 
 * @author Michele Carotta
 *
 */
public final class Config extends PropertiesLoader {
	private static final String CONFIG_FILENAME = "/sdk.properties";

	/**
	 * This static method load properly the configuration file.
	 * 
	 * @return a correct instance of Config, otherwise null.
	 * Thus, if you have an instance, the configure file is properly loaded.
	 */
	public static Config loadConfiguration() {
		final Config tmp_config = new Config();
		if (tmp_config.load())
			return tmp_config;
		else
			return null;
	}

	@Override
	protected String getFileName() {
		return CONFIG_FILENAME;
	}

	/**
	 * 
	 * @param config_entry the entry needed from the properties file.
	 * @return the specified property value.
	 */
	public String get(final CONFIG_ENTRY config_entry) {
		return properties.getProperty(config_entry.toString().toLowerCase());
	}

	/**
	 * 
	 * @param config_entry the entry needed from the properties file.
	 * @return the specified property value.
	 */
	public int getInt(final CONFIG_ENTRY config_entry) {
		try {
			return Integer.parseInt(get(config_entry));
		}
		catch (NumberFormatException ex) {
		}
		return 0;
	}

	/**
	 * 
	 * Check all the parameters in the properties file.
	 * @return null if no error occurs, the properties where error occurs otherwise.
	 */
	@Override
	protected String checkProperties() {
		for (final CONFIG_ENTRY item : CONFIG_ENTRY.values()) {
			if (item.mandatory() && !properties.containsKey(item.toString().toLowerCase()))
				return item.toString().toLowerCase();
		}
		return null;
	}

}