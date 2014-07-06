package com.oe.sdk.connection;

import com.oe.sdk.config.CONFIG_ENTRY;
import com.oe.sdk.config.Config;
import com.oe.sdk.exceptions.SMSCConnectionException;
import com.oe.sdk.exceptions.SMSCException;
import com.oe.sdk.exceptions.SMSCNotConfiguredException;

/**
 * 
 * @author Michele Carotta
 *
 */
public class SMSCConnectionFactory {
	private static final String CONNECTIONS_IMPL_PACKAGE = "com.oe.sdk.connection.";
	private static Config config;
	private static boolean inited = false;

	static {	// This block is executed only the first time the class is loaded.
		reinit();
	}

	/**
	 * This is the initialization method.
	 * Loads the SMSC Configuration.
	 * 
	 * @return true of false
	 */
	public static boolean init() {
		reinit();
		return inited;
	}

	/**
	 * Forces the re-loading of the SMSC Configuration.
	 */
	private static void reinit() {
		SMSCConnectionFactory.config = Config.loadConfiguration();
		inited = SMSCConnectionFactory.config != null;
	}

	/**
	 * Recalls explicitly the <code>init</code> method.
	 * @throws SMSCNotConfiguredException
	 */
	private static void checkinit() throws SMSCNotConfiguredException {
		if (!inited) {
			reinit();
		}
		if (!inited)
			throw new SMSCNotConfiguredException("Unable to load SMSC Configuration, see log for details");
	}

	/**
	 * The function provides the opening of the connection.
	 * 
	 * @throws SMSCNotConfiguredException
	 * @throws SMSCConnectionException
	 */
	public static SMSCConnection openConnection() throws SMSCException {
		checkinit();
		return openConnection(config.get(CONFIG_ENTRY.USERNAME),config.get(CONFIG_ENTRY.PASSWORD));
	}

	/**
	 * The function provides the opening of the connection, implementing a reflection pattern.
	 * 
	 * @param username login username
	 * @param password login password
	 * @throws SMSCNotConfiguredException
	 * @throws SMSCConnectionException
	 */
	public static SMSCConnection openConnection(final String username, final String password) throws SMSCException {
		checkinit();
		String className = null;
		SMSCConnection connection = null;
		try {
			className = CONNECTIONS_IMPL_PACKAGE + "SMSC" + config.get(CONFIG_ENTRY.CONNECTION_TYPE) + "Connection";
			final Class<? extends SMSCConnection> conn_class = Class.forName(className).asSubclass(SMSCConnection.class);
			connection = conn_class.newInstance();
			connection.login(config.get(CONFIG_ENTRY.HOSTNAME), Integer.parseInt(config.get(CONFIG_ENTRY.DEFAULT_PORT)), username, password, config.get(CONFIG_ENTRY.PROXY_ADDRESS), config.getInt(CONFIG_ENTRY.PROXY_PORT));
		} catch (final ClassNotFoundException cnfe) {
			System.err.println("ERROR - unable to find class file for Class " + className + ", check configuration file (is '" + config.get(CONFIG_ENTRY.CONNECTION_TYPE) + "' connection type available?)");
			throw new SMSCException(cnfe);
		} catch (final IllegalAccessException iae) {
			System.err.println("ERROR - can't load class file for Class " + className);
			throw new SMSCException(iae);
		} catch (final InstantiationException ie) {
			System.err.println("ERROR - can't instantiate Class " + className);
			throw new SMSCException(ie);
		}
		return connection;
	}

}