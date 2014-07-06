package com.oe.sdk.connection;

import java.util.Date;
import java.util.List;

import com.oe.sdk.exceptions.SMSCRemoteException;
import com.oe.sdk.model.Credit;
import com.oe.sdk.model.CreditMovement;
import com.oe.sdk.model.LookupResult;
import com.oe.sdk.model.MessageStatus;
import com.oe.sdk.model.SMS;
import com.oe.sdk.model.SMSRecipient;
import com.oe.sdk.model.SMS_MO;
import com.oe.sdk.model.SendResult;
import com.oe.sdk.model.SentSMS;
import com.oe.sdk.model.Subaccount;


/**
 * The <code>SMSCConnection</code> interface provides the general methods
 * offered by the SMS system: SMS sending, SMSs status and credit retrieving,
 * Mobile Originated SMSs, number look-up, etc.
 * The connection type implementation is left to the class that implements the interface.
 * 
 * @author Michele Carotta
 */
public interface SMSCConnection {
	public void login(String host, int port, String username, String password) throws SMSCRemoteException;
	public void login(String host, int port, String username, String password, String proxy, int proxyport) throws SMSCRemoteException;
	public void logout() throws SMSCRemoteException;
	public boolean isConnected();

	/**
	 * The function uses the connection to the server
	 * to send the object <code>SMS</code>.
	 * Returns an object <code>SendResult</code> with
	 * information about the sending result.
	 */
	public SendResult sendSMS(SMS sms) throws SMSCRemoteException;

	/**
	 * This function removes a scheduled message created
	 * with the <code>sendSMS</code> function;
	 * If the order_id is not found, this method returns
	 * successfully anyway.
	 */
	public boolean removeScheduledSend(String order_id) throws SMSCRemoteException;

	/**
	 * The function uses the connection to the server
	 * to obtain the array of <code>MessageStatus</code> objects
	 * relative to the specified ID message passed as argument.
	 * Each <code>MessageStatus</code> object provides
	 * for a single recipient.
	 */
	public List<MessageStatus> getMessageStatus(String order_id) throws SMSCRemoteException;

	/**
	 * The function asks the server if the number passed
	 * as argument is a valid recipient.
	 */
	public LookupResult lookup(SMSRecipient recipient) throws SMSCRemoteException;

	/**
	 * The function uses the connection to the server
	 * to retrieve the informations relative to the credits.
	 * With this informations builds an array of
	 * objects <code>Credit</code>, one for each
	 * different type of credit.
	 */
	public List<Credit> getCredits() throws SMSCRemoteException;

	/**
	 * The function uses the connection to the server
	 * to retrieve the informations relative to the Mobile Originated SMSs.
	 */
	public List<SMS_MO> getNewSMS_MOs() throws SMSCRemoteException;

	/**
	 * The function uses the connection to the server
	 * to retrieve the informations relative to the Mobile Originated SMSs.
	 * received in the gap between the two dates passed as arguments.
	 */
	public List<SMS_MO> getSMS_MOHistory(Date from_date, Date to_date) throws SMSCRemoteException;

	public List<SMS_MO> getSMS_MOById(long id_message) throws SMSCRemoteException;

	/**
	 * The function uses the connection to the server to
	 * retrieve the array of <code>SMS</code> executed in the
	 * gap between the two dates passed as arguments.
	 */
	public List<SentSMS> getSMSHistory(Date from_date, Date to_date) throws SMSCRemoteException;



	// SUBACCOUNTS-RELATED FUNCTIONS
	
	/**
	 * The function retrieves the array of <code>Subaccount</code> of the current user
	 */
	public List<Subaccount> getSubaccounts() throws SMSCRemoteException;
	/**
	 * The function creates a new <code>Subaccount</code> for the current user
	 */
	public Subaccount createSubaccount(Subaccount subaccount) throws SMSCRemoteException;
	/**
	 * The function activates a <code>Subaccount</code> of the current user
	 */
	public Subaccount lockSubaccount(Subaccount subaccount) throws SMSCRemoteException;
	/**
	 * The function disactivates a <code>Subaccount</code> of the current user
	 */
	public Subaccount unlockSubaccount(Subaccount subaccount) throws SMSCRemoteException;
	/**
	 * The function retrieves the array of <code>Credit</code> of the specified subaccount
	 */
	public List<Credit> getSubaccountCredits(Subaccount subaccount) throws SMSCRemoteException;
	/**
	 * The function moves the specified credits from the user to the subaccount or viceversa  
	 */
	public void moveCredits(CreditMovement credit_movement) throws SMSCRemoteException;
	/**
	 * The function creates a new purchase for the specificed subaccount
	 */
	public void createPurchase(CreditMovement credit_movement) throws SMSCRemoteException;
	/**
	 * The function clears a purchase of the specificed subaccount
	 */
	public void deletePurchase(CreditMovement credit_movement) throws SMSCRemoteException;
	/**
	 * The function retrieves the array of <code>CreditMovements</code> of the specified subaccount
	 */
	public List<CreditMovement> getPurchases(Subaccount subaccount) throws SMSCRemoteException;
	
}
