package com.oe.sdk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import com.oe.sdk.config.Nations;
import com.oe.sdk.exceptions.RequestError;
import com.oe.sdk.exceptions.SMSCRemoteException;
import com.oe.sdk.model.CreditType;
import com.oe.sdk.model.MessageStatus;
import com.oe.sdk.model.Nation;
import com.oe.sdk.model.SMSRecipient;
import com.oe.sdk.model.SMSSender;
import com.oe.sdk.model.SMSType;

public class ResponseParser {
	private static final char SEPARATOR = '|';
	private static final char NEWLINE = ';';
	private final char[] response;
	private int cursor;
	private boolean isok = false;
	private int errcode;
	private String errmsg;

	public ResponseParser(final String str_response) throws SMSCRemoteException {
//		System.out.println(str_response);
		response = str_response.toCharArray();
		cursor = 0;
		if (response.length >= 2) {
			final String stat = getNextString();
			if ("OK".equals(stat)) {
				isok = true;
			}
			if ("KO".equals(stat)) {
				isok = false;
				errcode = getNextInt();
				errmsg = getNextString();
			}
		} else {
			isok = false;
			errcode = 0;
			errmsg = "empty response received";
		}
	}

	public boolean nextLine() {
		while (response[cursor++] != NEWLINE) {
			if (cursor >= response.length)
				return false;
		}
		return response.length != cursor;
	}

	public String getNextString() throws SMSCRemoteException {
		final StringBuilder sb = new StringBuilder();
		while ((response[cursor] != SEPARATOR) && (response[cursor] != NEWLINE)) {
			sb.append(response[cursor]);
			cursor++;
			if (cursor >= response.length) {
				break;
			}
		}
		if ((cursor < response.length) && (response[cursor] != NEWLINE)) {
			cursor++;
		}
		String res = null;
		try {
			res = URLDecoder.decode(sb.toString(), "UTF-8");
		} catch (final UnsupportedEncodingException uee) { }
		return res;
	}
	public int getNextInt() throws SMSCRemoteException {
		final String str_i = getNextString();
		try {
			return Integer.parseInt(str_i);
		} catch (final NumberFormatException nfe) {
			throw new SMSCRemoteException(RequestError.RESPONSE_ERROR,nfe.getMessage());
		}
	}
	public long getNextLong() throws SMSCRemoteException {
		final String str_i = getNextString();
		try {
			return Long.parseLong(str_i);
		} catch (final NumberFormatException nfe) {
			throw new SMSCRemoteException(RequestError.RESPONSE_ERROR,nfe.getMessage());
		}
	}
	public Date getNextDate() throws SMSCRemoteException {
		return DDate.parse(getNextString());
	}
	public SMSRecipient getNextSMSRecipient() throws SMSCRemoteException {
		return new SMSRecipient(getNextString());
	}
	public SMSType getNextSMSType() throws SMSCRemoteException {
		final String str_sms_type = getNextString();
		try {
			return SMSType.fromCode(str_sms_type);
		} catch (final IllegalArgumentException iae) {
			throw new SMSCRemoteException(RequestError.RESPONSE_ERROR,"Invalid SMS type: "+str_sms_type);
		}
	}
	public SMSSender getNextSMSSender() throws SMSCRemoteException {
		return new SMSSender(getNextString());
	}
	public CreditType getNextCreditType() throws SMSCRemoteException {
		final String str_credit_type = getNextString();
		try {
			return CreditType.fromCode(str_credit_type);
		} catch (final IllegalArgumentException iae) {
			throw new SMSCRemoteException(RequestError.RESPONSE_ERROR,"Invalid credit type: "+str_credit_type);
		}
	}
	public MessageStatus.Status getNextMessageStatus_Status() throws SMSCRemoteException {
		try {
			return MessageStatus.Status.valueOf(getNextString());
		} catch (final IllegalArgumentException iae) {
			return MessageStatus.Status.UNKNOWN;
		}
	}
	public Nation getNextNation() throws SMSCRemoteException {
		final String str_iso3166 = getNextString();
		if (!Str.isEmpty(str_iso3166))
			return Nations.getInstance().getByISO3166(str_iso3166);
		else
			return null;
	}
	public boolean getNextBoolean() throws SMSCRemoteException {
		return Boolean.valueOf(getNextString());
	}

	public boolean isOk() {
		return isok;
	}
	public int getErrorCode() {
		return errcode;
	}
	public String getErrorMessage() {
		return errmsg;
	}
}

