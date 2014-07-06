package com.oe.sdk.connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.oe.sdk.config.Nations;
import com.oe.sdk.exceptions.RequestError;
import com.oe.sdk.exceptions.SMSCRemoteException;
import com.oe.sdk.model.Credit;
import com.oe.sdk.model.CreditMovement;
import com.oe.sdk.model.CreditType;
import com.oe.sdk.model.LookupResult;
import com.oe.sdk.model.MessageStatus;
import com.oe.sdk.model.Nation;
import com.oe.sdk.model.SMS;
import com.oe.sdk.model.SMSRecipient;
import com.oe.sdk.model.SMSSender;
import com.oe.sdk.model.SMSType;
import com.oe.sdk.model.SMS_MO;
import com.oe.sdk.model.SendResult;
import com.oe.sdk.model.SentSMS;
import com.oe.sdk.model.Subaccount;

public class SMSCDummyConnection implements SMSCConnection {
	private boolean connected = false;
	private long id_user;
	private final Random r = new Random();
	private Map<Long,List<Credit>> credits;
	private int order_id = 0;
	public void login(final String host, final int port, final String username, final String password) throws SMSCRemoteException {
		login(host, port, username, password, null, 0);
	}
	public void login(final String host, final int port, final String username, final String password, final String proxy, final int proxyport) throws SMSCRemoteException {
		connected = true;
		try {
			id_user = Long.parseLong(username);
		} catch (final NumberFormatException nfe) {
			throw new SMSCRemoteException(1,"Invalid username or password!");
		}
	}

	public void logout() throws SMSCRemoteException {
		checkConnected();
		connected = false;
	}

	public boolean isConnected() {
		return connected;
	}
	private void checkConnected() throws SMSCRemoteException {
		if (!connected)
			throw new SMSCRemoteException(RequestError.CONNECTION_ERROR,"NOT CONNECTED!!!");
	}
	public List<Credit> getCredits() throws SMSCRemoteException {
		checkConnected();
		List<Credit> usercredits = credits.get(id_user);
		if (usercredits == null) {
			credits.put(id_user, usercredits);
			usercredits = new ArrayList<Credit>();
			for (final CreditType credittype : CreditType.values()) {
				if (r.nextBoolean()) {
					usercredits.add(new Credit(credittype,randomNation(),r.nextInt(1000)));
				}
			}
		}
		return usercredits;
	}

	public SendResult sendSMS(final SMS sms) throws SMSCRemoteException {
		checkConnected();
		if (r.nextInt(10)>2)
			return new SendResult(nextid(),sms.getSmsRecipients().size());
		else
			throw new SMSCRemoteException(2,"Not enough credits to send these smss");
	}

	public boolean removeScheduledSend(final String order_id) throws SMSCRemoteException {
		return true;
	}

	public List<MessageStatus> getMessageStatus(final String order_id) throws SMSCRemoteException {
		checkConnected();
		final int size = 100+r.nextInt(900);
		final List<MessageStatus> mss = new ArrayList<MessageStatus>(size);
		for (int i=0;i<size;i++) {
			final MessageStatus.Status status = r.nextBoolean()?MessageStatus.Status.DLVRD:r.nextBoolean()?MessageStatus.Status.SENT:MessageStatus.Status.ERROR;
			mss.add(new MessageStatus(randomSMSRecpient(),status,status==MessageStatus.Status.DLVRD?randomDate_m():null));
		}
		return mss;
	}

	public List<SentSMS> getSMSHistory(final Date from_date, final Date to_date) throws SMSCRemoteException {
		checkConnected();
		final int size = 100+r.nextInt(900);
		final List<SentSMS> sentsmss = new ArrayList<SentSMS>(size);
		for (int i=0;i<size;i++) {
			final SMSType type = SMSType.values()[r.nextInt(SMSType.values().length)];
			sentsmss.add(new SentSMS(nextid(),randomDate_d(),type,new SMSSender(nextid()),r.nextInt(1000),null));
		}
		return sentsmss;
	}

	public List<SMS_MO> getNewSMS_MOs() throws SMSCRemoteException {
		checkConnected();
		return getSMS_MOList();
	}

	public List<SMS_MO> getSMS_MOById(final long id_message) throws SMSCRemoteException {
		checkConnected();
		return getSMS_MOList();
	}

	public List<SMS_MO> getSMS_MOHistory(final Date from_date, final Date to_date) throws SMSCRemoteException {
		checkConnected();
		return getSMS_MOList();
	}

	public LookupResult lookup(final SMSRecipient recipient) throws SMSCRemoteException {
		checkConnected();
		final Nation n = randomNation();
		return new LookupResult(nextid(),randomSMSRecpient(n),n,n.toString(),nextid());
	}

	private Nation randomNation() {
		final List<Nation> ro_nations = Nations.getInstance().getNations();
		return ro_nations.get(r.nextInt(ro_nations.size()));
	}
	private SMSRecipient randomSMSRecpient() {
		return randomSMSRecpient(randomNation());
	}
	private SMSRecipient randomSMSRecpient(final Nation nation) {
		return new SMSRecipient("+"+nation.getPrefix()+(1000000+r.nextInt()%8999999));
	}
	private Date randomDate_m() {
		return new Date(System.currentTimeMillis()-Math.abs(r.nextInt(1000000)));
	}
	private Date randomDate_d() {
		return new Date(System.currentTimeMillis()-(100000000+Math.abs(new Random().nextInt(1000000000))));
	}
	private String nextid() {
		return Integer.toHexString(order_id++);
	}
	private List<SMS_MO> getSMS_MOList() {
		final int size = 100+r.nextInt(900);
		final List<SMS_MO> sms_mo = new ArrayList<SMS_MO>(size);
		for (int i=0;i<size;i++) {
			sms_mo.add(new SMS_MO(r.nextLong(),randomSMSRecpient(),new SMSSender(nextid()),nextid(),randomDate_d(),null));
		}
		return sms_mo;
	}

	@Override
	public Subaccount createSubaccount(Subaccount subaccount) throws SMSCRemoteException {
		subaccount.setLogin("sub_"+new Random().nextInt(100));
		subaccount.setPassword("password");
		return subaccount;
	}

	@Override
	public List<Subaccount> getSubaccounts() throws SMSCRemoteException {
		List<Subaccount> subaccounts = new ArrayList<Subaccount>();
		Random r = new Random();
		for (int i=0;i<10;i++) {
			Subaccount subaccount = new Subaccount();
			subaccount.setActive(r.nextBoolean());
			subaccount.setAddress("address_"+i);
			subaccount.setCity("city"+i);
			subaccount.setCompany_name("company_"+i);
			subaccount.setCredit_mode(r.nextBoolean()?1:0);
			subaccount.setEmail("subaccount_"+i+"@example.com");
			subaccount.setFiscal_code("fiscal_code_"+i);
			subaccount.setLogin("login_"+i);
			subaccount.setMobile("333"+r.nextInt(10000000)+10000000);
			subaccount.setName("name_"+i);
			subaccount.setSurname("surname_"+i);
			subaccount.setProvince("province_"+i);
			subaccount.setStr_subaccountType("company");
			subaccount.setVat_number("vat_number_"+i);
			subaccount.setZip("zip_"+i);
			subaccounts.add(subaccount);
		}
		return subaccounts;
	}

	@Override
	public Subaccount lockSubaccount(Subaccount subaccount) throws SMSCRemoteException {
		subaccount.setActive(false);
		return subaccount;
	}

	@Override
	public Subaccount unlockSubaccount(Subaccount subaccount) throws SMSCRemoteException {
		subaccount.setActive(true);
		return subaccount;
	}

	@Override
	public void moveCredits(CreditMovement credit_movement) throws SMSCRemoteException {
	}

	@Override
	public List<Credit> getSubaccountCredits(Subaccount subaccount) throws SMSCRemoteException {
		return getCredits();
	}

	@Override
	public void createPurchase(CreditMovement credit_movement) throws SMSCRemoteException {
		
	}

	@Override
	public void deletePurchase(CreditMovement credit_movement) throws SMSCRemoteException {
		
	}

	@Override
	public List<CreditMovement> getPurchases(Subaccount subaccount) throws SMSCRemoteException {
		return null;
	}

}
