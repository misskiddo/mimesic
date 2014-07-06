package com.oe.sdk.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The class <code>SMS_MO</code> (<i>SMS Mobile Originated</i>) provides for incoming SMSs.
 *
 * @author Michele Carotta
 */
public class SMS_MO implements Serializable {
	private static final long serialVersionUID = -5080506454654933147L;
	private long id_message;				// SMS sending date
	private final Date send_date;				// SMS sending date
	private final String message;				// SMS text message
	private String keyword;				// SMS's text first word, if used as a keyword, otherwise null
	private final SMSSender sms_sender;			// SMS sender
	private final SMSRecipient sms_recipient;	// SMS recipient

	public SMS_MO(final long id_message, final SMSRecipient sms_recipient, final SMSSender sms_sender, final String message, final Date send_date, final String keyword) {
		this.id_message = id_message;
		this.sms_recipient = sms_recipient;
		this.sms_sender = sms_sender;
		this.send_date = send_date;
		this.message = message;
	}

	public Date getSend_date() {
		return send_date;
	}
	public SMSSender getSms_sender() {
		return sms_sender;
	}
	public SMSRecipient getSms_recipient() {
		return sms_recipient;
	}
	public String getMessage() {
		return message;
	}
	public long getId_message() {
		return id_message;
	}
	public String getKeyword() {
		return keyword;
	}

	@Override
	public String toString() {
		return new StringBuilder()
		.append("(send_date=").append(send_date)
		.append(",message=").append(message)
		.append(",sms_sender=").append(sms_sender)
		.append(",sms_recipient=").append(sms_recipient).append(')').toString();
	}

}
