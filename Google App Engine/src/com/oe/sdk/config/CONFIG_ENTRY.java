package com.oe.sdk.config;

public enum CONFIG_ENTRY {
	HOSTNAME (true),
	USERNAME (false),
	PASSWORD (false),
	CONNECTION_TYPE (true),
	DEFAULT_PORT (true),
	PROXY_ADDRESS (false),
	PROXY_PORT (false);

	private boolean mandatory;

	private CONFIG_ENTRY(final boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean mandatory() {
		return mandatory;
	}

}
