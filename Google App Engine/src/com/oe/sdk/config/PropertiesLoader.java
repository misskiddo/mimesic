package com.oe.sdk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.oe.sdk.util.Str;

/**
 * The class <code>PropertiesLoader</code> provides an options initializing reliable system.
 * 
 * @author Michele Carotta
 */
public abstract class PropertiesLoader {
	protected Properties properties;										// the Properties file.
	private static final String[] FILE_PATHS = { ".", "config"};	// the path list.
	@SuppressWarnings({ "rawtypes" }) // 'cause we just need a generic Class
	private static final Class resources_class = PropertiesLoader.class;	// we try to load the .properties files
	// from the same .jar where this class is stored
	protected abstract String getFileName();						// the file name of the properties file.

	/**
	 * We let the child classes to provide the proper checking mechanism.
	 * 
	 * @return the key inside which the error occurs, otherwise null.
	 */
	protected abstract String checkProperties();

	/**
	 * We let the child classes to provide the proper checking mechanism.
	 * 
	 * @return the proper <code>Properties</code> instance
	 */
	protected Properties getDefaultProperties() { return null; }

	/**
	 * The function try to load the Properties file in sequence: first from the the file,
	 * and then from the JAR. For both options a list of path are checked.
	 * In case of failure, the function call the <code>defaultPropertiesLoader</code> function.
	 * 
	 * @return true if the Properties file is properly loaded, otherwise null.
	 */
	protected boolean load() {
		properties = null;
		// Try to read from the properties file.
		for (int i=0; (i<FILE_PATHS.length) && (properties==null); i++) {
			properties = loadPropertiesFromFile(FILE_PATHS[i] + Str.sep() + getFileName());
		}
		// Try to load file properties from the JAR.
		if (properties == null) {
			properties = loadPropertiesFromClassLoader(getFileName());
		}
		// if unable to load the properties, just load the default ones
		if (properties == null) {
			properties = getDefaultProperties();
		}
		// If 'props' is still null, exit.
		if (properties == null) {
			System.err.println("ERROR - Unable to load configuration file.");
			return false;
		}
		// Checking that everything in the properties file is all right.
		String tmp_config_key;
		if ((tmp_config_key = checkProperties()) != null) {
			System.err.println("ERROR - wrong value or missing mandatory configuration key: "+tmp_config_key);
			return false;
		}
		return true;
	}

	/**
	 * The function load the properties from the specified JAR.
	 * @param fileProperties the JAR filename to load
	 * @return the proper <code>Properties</code> instance
	 */
	private Properties loadPropertiesFromClassLoader(final String fileProperties) {
		Properties properties = null;
		try {
			final InputStream is = resources_class.getResourceAsStream(fileProperties);
			if (is == null)
				return null;
			properties = new Properties();
			properties.load(is); // Load file properties from JAR.
		} catch (final IOException e) {
			System.err.println("Unable to read configuration from archive: " + e.getMessage());
			return null;
		}
		return properties;
	}

	/**
	 * The function load the properties from the specified file.
	 * @param str_properties_file the properties filename to load
	 * @return the proper <code>Properties</code> instance
	 */
	private Properties loadPropertiesFromFile(final String str_properties_file) {
		Properties properties = null;
		try {
			final File properties_file = new File(str_properties_file);
			if (properties_file.exists() && properties_file.isFile() && properties_file.canRead()) {
				properties = new Properties();
				properties.load(new FileInputStream(str_properties_file));
			}
		} catch (final FileNotFoundException e) { // we check file existence, so this exception should never be raised
			return null;
		} catch (final IOException e) {
			System.err.println("Unable to read from file " + str_properties_file + ":" + e.getMessage());
			return null;
		}
		return properties;
	}

	// let's hide the constructor
	protected PropertiesLoader() { };
}
