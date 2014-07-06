package com.oe.sdk.exceptions;

public class SMSCException extends Exception {
	private static final long serialVersionUID = -7284881094853974001L;

	public SMSCException() {
		super();
	}
	public SMSCException(final String message) {
		super(message);
	}

	public SMSCException(final Throwable cause) {
		super(cause);
	}

}
