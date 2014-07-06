package com.oe.sdk.exceptions;

public class SMSCRemoteException extends SMSCException {
	private static final long serialVersionUID = -2497268553120518810L;
	private final int code;

	public SMSCRemoteException(final int code, final String _message) {
		super(_message);
		this.code = code;
	}

	public int getErrorCode() {
		return code;
	}

}
