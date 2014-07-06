package com.oe.sdk.model;

import java.util.Date;

public class SentSMS {
	private final String order_id;
	private final Date create_time;
	private final SMSType sms_type;
	private final SMSSender sender;
	private final int recipients_count;
	private final Date scheduled_send;

	public SentSMS(final String order_id, final Date create_time, final SMSType sms_type, final SMSSender sender, final int recipients_count, final Date scheduled_send) {
		this.create_time = create_time;
		this.order_id = order_id;
		this.recipients_count = recipients_count;
		this.scheduled_send = scheduled_send;
		this.sender = sender;
		this.sms_type = sms_type;
	}

	public String getOrder_id() {
		return order_id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public SMSType getSms_type() {
		return sms_type;
	}
	public SMSSender getSender() {
		return sender;
	}
	public int getRecipients_count() {
		return recipients_count;
	}
	public Date getScheduled_send() {
		return scheduled_send;
	}

}
