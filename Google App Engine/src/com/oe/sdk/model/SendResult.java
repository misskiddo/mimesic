package com.oe.sdk.model;

/**
 * 
 * @author Michele Carotta
 */
public class SendResult {
	private final int sent_smss;			// The number of reached recipients
	private final String order_id;			// sms order ID

	public SendResult(final String order_id, final int sent_smss) {
		this.order_id = order_id;
		this.sent_smss = sent_smss;
	}

	public int getSent_smss() {
		return sent_smss;
	}

	public String getOrder_id() {
		return order_id;
	}

	@Override
	public String toString() {
		return new StringBuilder()
		.append("(order_id=").append(order_id)
		.append(",sent_smss=").append(sent_smss)
		.append(')').toString();
	}

}
