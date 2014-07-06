package com.oe.sdk.model;

import java.util.Date;

/**
 * The class <code>MessageStatus</code> provides for date and status
 * (<code>SENT, READY, WAITING, etc</code>) of the message delivery.
 * @author Michele Carotta
 *
 */
public class MessageStatus {
	private final SMSRecipient rcpt_number;		// SMS recipient
	private final MessageStatus.Status status;	// Message status
	private final Date deliveryDate;				// Delivery date

	public MessageStatus(final SMSRecipient rcpt_number, final MessageStatus.Status status, final Date deliveryDate) {
		this.rcpt_number = rcpt_number;
		this.status = status;
		this.deliveryDate = deliveryDate;
	}

	public SMSRecipient getRcpt_number() {
		return rcpt_number;
	}
	public Status getStatus() {
		return status;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	@Override
	public String toString() {
		return new StringBuilder()
		.append("(rcpt_number=").append(rcpt_number)
		.append(",status=").append(status)
		.append(",deliveryDate=").append(deliveryDate==null?"*immediate*":deliveryDate)
		.append(')').toString();
	}

	public enum Status {
		SCHEDULED,	// postponed, not jet arrived
		SENT,		// sent, wait for delivery notification (depending on message type)
		DLVRD,		// the sms has been correctly delivered to the mobile phone
		ERROR,		// error sending sms
		TIMEOUT,	// cannot deliver sms to the mobile in 48 hours
		TOOM4NUM,	// too many messages sent to this number (spam warning)
		TOOM4USER,	// too many messages sent by this user
		UNKNPFX,	// unknown/unparsable mobile phone prefix
		UNKNRCPT,	// unknown recipient
		WAIT4DLVR,	// message sent, waiting for delivery notification
		WAITING,	// not yet sent (still active)
		UNKNOWN;		// received an unknown status code from server (should never happen!)

		public boolean isError() {
			switch (this) {
			case ERROR:
			case TIMEOUT:
			case TOOM4NUM:
			case TOOM4USER:
			case UNKNPFX:
			case UNKNRCPT:
				return true;
			}
			return false;
		}
	}

}
