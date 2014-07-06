package com.oe.sdk.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oe.sdk.exceptions.RequestError;
import com.oe.sdk.exceptions.SMSCRemoteException;
import com.oe.sdk.model.Credit;
import com.oe.sdk.model.CreditMovement;
import com.oe.sdk.model.CreditType;
import com.oe.sdk.model.LookupResult;
import com.oe.sdk.model.MessageStatus;
import com.oe.sdk.model.SMS;
import com.oe.sdk.model.SMSRecipient;
import com.oe.sdk.model.SMSType;
import com.oe.sdk.model.SMS_MO;
import com.oe.sdk.model.SendResult;
import com.oe.sdk.model.SentSMS;
import com.oe.sdk.model.Subaccount;
import com.oe.sdk.util.DDate;
import com.oe.sdk.util.HTTP;
import com.oe.sdk.util.ResponseParser;
import com.oe.sdk.util.Str;
import com.oe.sdk.util.HTTP.HTTPException;

public class SMSCHTTPConnection implements SMSCConnection {
	private String username;
	private String password;
	private String hostname;
	private String proxy;
	private boolean connected;
	private int proxyport;

	public void login(String hostname, final int port, final String username, final String password) throws SMSCRemoteException {
		login(hostname, port, username, password, null, 0);
	}
	public void login(String hostname, final int port, final String username, final String password, final String proxy, final int proxyport) throws SMSCRemoteException {
		this.username = username;
		this.password = password;
		this.proxy = proxy;
		this.proxyport = proxyport;
			boolean https = false;
		if (hostname.startsWith("https://")) {
			https = true;
		}
		if (!https && !hostname.startsWith("http://")) {
			hostname = "http://"+hostname;
		}
		if (hostname.endsWith("/")) {
			hostname = hostname.substring(0,hostname.length()-1);
		}
		if (((https && port != 443) || (!https && port != 80)) && (port != 0)) {
			hostname = hostname+":"+port;
		}
		this.hostname = hostname;
		connected = false;
		getCredits(); // just to check username+password correctness ...
		connected = true;
	}
	public void logout() throws SMSCRemoteException {
		connected = false;
	}

	public List<Credit> getCredits() throws SMSCRemoteException {
		ResponseParser rp;
		Credit credit;
		List<Credit> credits;
		CreditType credit_type;
		rp = makeStdHTTPRequest(CREDITS_REQ,getBaseHTTPParams());
		credits = new ArrayList<Credit>();
		while (rp.nextLine()) {
			credit_type = rp.getNextCreditType();
			credit = new Credit(credit_type,rp.getNextNation(),rp.getNextInt());
			credits.add(credit);
		}
		return Collections.unmodifiableList(credits);
	}

	public SendResult sendSMS(final SMS sms) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("message", sms.getMessage());
		params.put("message_type", sms.getSms_type().code());
		if (!Str.isEmpty(sms.getOrder_id())) {
			params.put("order_id", sms.getOrder_id());
		}
		if (sms.getSms_type().hasCustomTPOA()) {
			params.put("sender", sms.getSmsSender().getNumber());
		}
		final StringBuilder recipient_list = new StringBuilder();
		for (final SMSRecipient recipient : sms.getSmsRecipients()) {
			recipient_list.append(recipient.getNumber()).append(',');
		}
		params.put("recipient", recipient_list.toString());
		if (!sms.isImmediate()) {
			params.put("scheduled_delivery_time", DDate.format(sms.getScheduled_delivery()));
		}

		final ResponseParser rp = makeStdHTTPRequest(SEND_SMS_REQ,params);
		return new SendResult(rp.getNextString(),rp.getNextInt());
	}

	public boolean removeScheduledSend(final String order_id) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("order_id", order_id);
		makeStdHTTPRequest(REMOVE_DELAYED_REQ,params);
		return true;
	}

	public List<MessageStatus> getMessageStatus(final String order_id) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("order_id", order_id);
		final ResponseParser rp = makeStdHTTPRequest(MSG_STATUS_REQ,params);
		final List<MessageStatus> statuses = new ArrayList<MessageStatus>();
		while (rp.nextLine()) {
			statuses.add(new MessageStatus(rp.getNextSMSRecipient(),rp.getNextMessageStatus_Status(),rp.getNextDate()));
		}
		return Collections.unmodifiableList(statuses);
	}

	public List<SentSMS> getSMSHistory(final Date from_date, final Date to_date) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("from",DDate.format(from_date));
		params.put("to",DDate.format(to_date));
		final ResponseParser rp = makeStdHTTPRequest(SMS_HISTORY_REQ,params);
		final List<SentSMS> sent_smss = new ArrayList<SentSMS>();
		while (rp.nextLine()) {
			sent_smss.add(new SentSMS(rp.getNextString(),rp.getNextDate(),rp.getNextSMSType(),rp.getNextSMSSender(),rp.getNextInt(),rp.getNextDate()));
		}
		return sent_smss;
	}

	public LookupResult lookup(final SMSRecipient recipient) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("num", recipient.getNumber());
		final ResponseParser rp = makeStdHTTPRequest(NUMBER_LOOKUP_REQ,params);
		final String order_id = rp.getNextString();
		final boolean valid = "valid".equals(rp.getNextString());
		LookupResult lr;
		if (valid) {
			lr = new LookupResult(order_id,rp.getNextSMSRecipient(),rp.getNextNation(),rp.getNextString(),rp.getNextString());
		} else {
			lr = new LookupResult(order_id);
		}
		return lr;
	}

	public List<SMS_MO> getNewSMS_MOs() throws SMSCRemoteException {
		return getAllSMS_MO(makeStdHTTPRequest(NEW_SMS_MO_REQ,getBaseHTTPParams()));
	}
	public List<SMS_MO> getSMS_MOHistory(final Date from_date, final Date to_date) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("date_from", DDate.format(from_date));
		params.put("date_to", DDate.format(to_date));
		return getAllSMS_MO(makeStdHTTPRequest(SMS_MO_HIST_REQ,params));
	}
	public List<SMS_MO> getSMS_MOById(final long message_id) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("id", String.valueOf(message_id));
		return getAllSMS_MO(makeStdHTTPRequest(SMS_MO_BYID_REQ,params));
	}

	public Subaccount createSubaccount(Subaccount subaccount) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","CREATE_SUBACCOUNT");
		params.put("credit_mode",String.valueOf(subaccount.getCredit_mode()));
		if (!Str.isEmpty(subaccount.getCompany_name())) {
			params.put("company_name",subaccount.getCompany_name());
		}
		params.put("fiscal_code",subaccount.getFiscal_code());
		params.put("vat_number",subaccount.getVat_number());
		params.put("name",subaccount.getName());
		params.put("surname",subaccount.getSurname());
		params.put("email",subaccount.getEmail());
		params.put("address",subaccount.getAddress());
		params.put("city",subaccount.getCity());
		params.put("province",subaccount.getProvince());
		params.put("zip",subaccount.getZip());
		params.put("mobile",subaccount.getMobile());
		if (!Str.isEmpty(subaccount.getPassword()))
			params.put("sub_password",subaccount.getPassword());
		final ResponseParser rp = makeStdHTTPRequest(SUBACCOUNTS_REQ,params);
		subaccount.setLogin(rp.getNextString());
		subaccount.setPassword(rp.getNextString());
		return subaccount;
	}
	public List<Subaccount> getSubaccounts() throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","LIST_SUBACCOUNTS");
		final ResponseParser rp = makeStdHTTPRequest(SUBACCOUNTS_REQ,params);
		List<Subaccount> subaccounts = new ArrayList<Subaccount>();
		Subaccount subaccount;
		while (rp.nextLine()) {
			subaccount = new Subaccount();
			subaccount.setLogin(rp.getNextString());
			subaccount.setActive(rp.getNextBoolean());
			subaccount.setCredit_mode(rp.getNextInt());
			subaccount.setStr_subaccountType(rp.getNextString());
			subaccount.setCompany_name(rp.getNextString());
			subaccount.setFiscal_code(rp.getNextString());
			subaccount.setVat_number(rp.getNextString());
			subaccount.setName(rp.getNextString());
			subaccount.setSurname(rp.getNextString());
			subaccount.setEmail(rp.getNextString());
			subaccount.setAddress(rp.getNextString());
			subaccount.setCity(rp.getNextString());
			subaccount.setProvince(rp.getNextString());
			subaccount.setZip(rp.getNextString());
			subaccount.setMobile(rp.getNextString());
			subaccounts.add(subaccount);
		}
		return subaccounts;
	}
	public Subaccount lockSubaccount(Subaccount subaccount) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","LOCK_SUBACCOUNT");
		params.put("subaccount",subaccount.getLogin());
		makeStdHTTPRequest(SUBACCOUNTS_REQ,params);		
		subaccount.setActive(false);
		return subaccount;
	}
	public Subaccount unlockSubaccount(Subaccount subaccount) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","UNLOCK_SUBACCOUNT");
		params.put("subaccount",subaccount.getLogin());
		makeStdHTTPRequest(SUBACCOUNTS_REQ,params);		
		subaccount.setActive(true);
		return subaccount;
	}
	public void moveCredits(CreditMovement credit_movement) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","MOVE_CREDITS");
		params.put("subaccount",credit_movement.getSubaccount_login());
		params.put("super_to_sub",String.valueOf(credit_movement.isSuper_to_sub()));
		params.put("amount",String.valueOf(credit_movement.getAmount()));
		params.put("message_type",credit_movement.getSms_type().code());
		makeStdHTTPRequest(SUBACCOUNTS_REQ,params);		
	}
	public List<Credit> getSubaccountCredits(Subaccount subaccount) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","GET_CREDITS");
		params.put("subaccount",subaccount.getLogin());
		final ResponseParser rp = makeStdHTTPRequest(SUBACCOUNTS_REQ,params);
		Credit credit;
		List<Credit> credits;
		CreditType credit_type;
		credits = new ArrayList<Credit>();
		while (rp.nextLine()) {
			credit_type = rp.getNextCreditType();
			credit = new Credit(credit_type,rp.getNextNation(),rp.getNextInt());
			credits.add(credit);
		}
		return Collections.unmodifiableList(credits);
	}
	public void createPurchase(CreditMovement credit_movement) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","CREATE_PURCHASE");
		params.put("subaccount",credit_movement.getSubaccount_login());
		ArrayList<String> mt = new ArrayList<String>();
		for (SMSType st : credit_movement.getSms_types())
			mt.add(st.code());
		params.put("message_types", Str.join(';', mt.toArray(new String[0])));

		ArrayList<String> ppm = new ArrayList<String>();
		for (double st : credit_movement.getPricePerMessage())
			ppm.add(Double.toString(st));
		params.put("price_per_messages", Str.join(';', ppm.toArray(new String[0])));
		
		params.put("price",Double.toString(credit_movement.getPrice()));
		makeStdHTTPRequest(SUBACCOUNTS_REQ,params);		
		
	}
	public void deletePurchase(CreditMovement credit_movement) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","DELETE_PURCHASE");
		params.put("subaccount",credit_movement.getSubaccount_login());
		params.put("id_purchase",String.valueOf(credit_movement.getId_purchase()));
		makeStdHTTPRequest(SUBACCOUNTS_REQ,params);		
	}
	public List<CreditMovement> getPurchases(Subaccount subaccount) throws SMSCRemoteException {
		final Map<String,String> params = getBaseHTTPParams();
		params.put("op","GET_PURCHASES");
		params.put("subaccount",subaccount.getLogin());
		final ResponseParser rp = makeStdHTTPRequest(SUBACCOUNTS_REQ,params);
		List<CreditMovement> credits = new ArrayList<CreditMovement>();
		while (rp.nextLine()) {
			CreditMovement cm = new CreditMovement();
			cm.setSubaccount_login(subaccount.getLogin());
			cm.setSuper_to_sub(rp.getNextBoolean());

			cm.setAmount((int)Double.parseDouble(rp.getNextString()));

			cm.setRecording_date(rp.getNextDate());
			cm.setId_purchase(rp.getNextLong());
			
			cm.setPrice(Double.parseDouble(rp.getNextString()));
			cm.setAvailableAmount(rp.getNextInt());
			
			String[] strTypes = rp.getNextString().split(";");
			SMSType[] sms_types = new SMSType[strTypes.length];
			for (int i=0; i<strTypes.length;i++)
				sms_types[i] = SMSType.fromCode(strTypes[i]);
			cm.setSms_types(sms_types);

			String[] ppm = rp.getNextString().split(";");
			double[] pricePerMessages = new double[ppm.length];
			for (int i=0; i<ppm.length;i++)
				pricePerMessages[i] = Double.parseDouble(ppm[i]);
			cm.setPricePerMessage(pricePerMessages);
			credits.add(cm);
		}
		return Collections.unmodifiableList(credits);
	}	

	public boolean isConnected() {
		return connected;
	}
	private ResponseParser makeStdHTTPRequest(final String request, final Map<String,String> params) throws SMSCRemoteException {
		String http_response = null;
		try {
			http_response = HTTP.POST(hostname+request, params, proxy, proxyport);
		} catch (final HTTPException httpe) {
			throw new SMSCRemoteException(RequestError.CONNECTION_ERROR,httpe.getMessage());
		}
		final ResponseParser rp = new ResponseParser(http_response);
		if (rp.isOk())
			return rp;
		else
			throw new SMSCRemoteException(rp.getErrorCode(), rp.getErrorMessage());
	}
	private Map<String,String> getBaseHTTPParams() {
		final Map<String,String> base_params = new HashMap<String,String>();
		base_params.put("login", username);
		base_params.put("password", password);
		return base_params;
	}
	private List<SMS_MO> getAllSMS_MO(final ResponseParser rp) throws SMSCRemoteException {
		final List<SMS_MO> new_smss = new ArrayList<SMS_MO>();
		while (rp.nextLine()) {
			new_smss.add(new SMS_MO(rp.getNextLong(),rp.getNextSMSRecipient(),rp.getNextSMSSender(),rp.getNextString(),rp.getNextDate(),rp.getNextString()));
		}
		return new_smss;
	}

	private static final String CREDITS_REQ = "/Trend/CREDITS";
	private static final String SEND_SMS_REQ = "/Trend/SENDSMS";
	private static final String REMOVE_DELAYED_REQ = "/Trend/REMOVE_DELAYED";
	private static final String MSG_STATUS_REQ = "/Trend/SMSSTATUS";
	private static final String SMS_HISTORY_REQ = "/Trend/SMSHISTORY";
	private static final String NUMBER_LOOKUP_REQ = "/OENL/NUMBERLOOKUP";
	private static final String NEW_SMS_MO_REQ = "/OESRs/SRNEWMESSAGES";
	private static final String SMS_MO_HIST_REQ = "/OESRs/SRHISTORY";
	private static final String SMS_MO_BYID_REQ = "/OESRs/SRHISTORYBYID";
	private static final String SUBACCOUNTS_REQ = "/Trend/SUBACCOUNTS";
}
