package com.oe.sdk.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.oe.sdk.exceptions.InvalidMessageContentException;
import com.oe.sdk.exceptions.InvalidRecipientException;
import com.oe.sdk.exceptions.InvalidSenderException;
import com.oe.sdk.exceptions.SMSCException;
import com.oe.sdk.util.Str;

/**
 * The <code>SMS</code> class is the abstract representation of an SMS (Short Message Service).
 * The class contains all the main data structures defined before.
 * It permits the user to add a sender, a list of recipients, to set the type of SMS,
 * the SMS text, the sending date (if we want postponed sending), the SMS ID (otherwise
 * the system will assign it remotely).
 * Moreover, the class provides methods for checking the content and validating the SMS.
 * 
 * @author Michele Carotta
 */
public class SMS implements Serializable {
	private static final long serialVersionUID = -3491023103655936959L;
	private String order_id;					// sms order ID
	private SMSType sms_type;					// SMS type
	private Date scheduled_delivery;			// scheduled send date (null if immediate)
	private String message;						// SMS text
	private SMSSender sms_sender;				// SMS sender
	private final List<SMSRecipient> sms_recipients;	// The list of recipients

	public SMS() {
		sms_recipients = new ArrayList<SMSRecipient>();
		sms_type = SMSType.GOLD_PLUS; // default SMS type is GOLD PLUS
	}

	/**
	 * The function encodes the sms message with GSM7bit character set
	 * 
	 * @param message the sms message
	 */
	public void setMessage(final String message) throws InvalidMessageContentException {
		final int sms_len = Str.sms_length(message);
		if ((sms_len==0)||(sms_len>1000))
			throw new InvalidMessageContentException("invalid message content length ("+sms_len+")");
		this.message = message;
	}

	/**
	 * The function decodes the SMS text message encoded with GSM7bit character set
	 * 
	 * @return a <code>String</code> containing the SMS text message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the number of characters of the sms message encoded in GSM7bit standard.
	 */
	public int length() {
		return Str.sms_length(message);
	}
	/**
	 * @return the number of smss that will be used to send this message
	 */
	public int count_smss() {
		return length() <= 160 ? 1 : ((length()-1)/153)+1;
	}

	/**
	 * Sets the message ID, assigned by the server when null
	 * @param id_message
	 */
	public void setOrder_id(final String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_id() {
		return order_id;
	}

	/**
	 * date for scheduled delivery
	 * @return
	 */
	public Date getScheduled_delivery() {
		return scheduled_delivery;
	}
	public void setScheduled_delivery(final Date scheduled_delivery) {
		this.scheduled_delivery = scheduled_delivery;
	}
	public void setImmediate() {
		scheduled_delivery = null;
	}
	public boolean isImmediate() {
		return scheduled_delivery == null;
	}

	/**
	 * one of the valid SMStrend message types
	 * @param smsType
	 */
	public void setSms_type(final SMSType sms_type) {
		this.sms_type = sms_type;
	}
	public SMSType getSms_type() {
		return sms_type;
	}

	public SMSSender getSmsSender() {
		return sms_sender;
	}
	public void setSms_sender(final SMSSender sender) throws InvalidSenderException {
		if(!sender.isValid())
			throw new InvalidSenderException();
		sms_sender = sender;
	}
	public void setSms_sender(final String str_sender) throws InvalidSenderException {
		this.setSms_sender(new SMSSender(str_sender));
	}


	/**
	 * @return an unmodifiable view of the sms recipients list
	 */
	public List<SMSRecipient> getSmsRecipients() {
		return Collections.unmodifiableList(sms_recipients);
	}
	/**
	 * The function checks the <code>SMSRrecipient</tt> and then adds it to the sms.
	 * 
	 * @param recipient the <code>SMSRrecipient</tt> of the sms
	 * @throws InvalidRecipientException
	 */
	public void addSmsRecipient(final SMSRecipient recipient) throws InvalidRecipientException {
		if (!recipient.isValid())
			throw new InvalidRecipientException();
		sms_recipients.add(recipient);
	}
	/**
	 * The function adds a sms recipient to the sms.
	 * 
	 * @param str_recipient the recipient phone number.
	 * @throws InvalidRecipientException
	 */
	public void addSmsRecipient(final String str_recipient) throws InvalidRecipientException {
		this.addSmsRecipient(new SMSRecipient(str_recipient));
	}

	/**
	 * Checks that sender, recipient, message and smsType are all OK.
	 * 
	 */
	public void validate() throws SMSCException {
		if(sms_type == null)
			throw new SMSCException("Invalid NULL message type");
		if (sms_type.hasCustomTPOA() && (sms_sender == null))
			throw new InvalidSenderException();
		if(sms_recipients.isEmpty())
			throw new InvalidRecipientException();
		if(message == null)
			throw new InvalidMessageContentException("message is empty");
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder()
		.append("(id_message=").append(order_id)
		.append(",smsType=").append(sms_type)
		.append(",send_date=").append(scheduled_delivery)
		.append(",message=").append(getMessage())
		.append(",smsSender=").append(sms_sender)
		.append(",smsRecipients:");
		int i=0;
		for(final SMSRecipient recipient : sms_recipients) {
			if (i++>0) {
				sb.append(',');
			}
			sb.append(recipient);
		}
		sb.append(')');
		return sb.toString();
	}

}
