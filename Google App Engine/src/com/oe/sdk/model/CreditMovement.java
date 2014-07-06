package com.oe.sdk.model;

import java.util.Date;

public class CreditMovement {
	private String subaccount_login;
	private boolean super_to_sub;
	private int amount;
	private SMSType sms_type;
	private SMSType[] sms_types;
	private double price;
	private double[] pricePerMessage;
	private boolean is_donation = false;
	private long id_purchase;
	private Date recording_date;
	private int availableAmount;

	public String getSubaccount_login() {
		return this.subaccount_login;
	}
	public void setSubaccount_login(String subaccount_login) {
		this.subaccount_login = subaccount_login;
	}
	public boolean isSuper_to_sub() {
		return this.super_to_sub;
	}
	public void setSuper_to_sub(boolean super_to_sub) {
		this.super_to_sub = super_to_sub;
	}
	public int getAmount() {
		return this.amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public SMSType getSms_type() {
		return this.sms_type;
	}
	public void setSms_type(SMSType sms_type) {
		this.sms_type = sms_type;
	}
	public SMSType[] getSms_types() {
		return sms_types;
	}
	public void setSms_types(SMSType[] sms_types) {
		this.sms_types = sms_types;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double[] getPricePerMessage() {
		return pricePerMessage;
	}
	public void setPricePerMessage(double[] pricePerMessage) {
		this.pricePerMessage = pricePerMessage;
	}
	public boolean isIs_donation() {
		return is_donation;
	}
	public void setIs_donation(boolean is_donation) {
		this.is_donation = is_donation;
	}
	public long getId_purchase() {
		return id_purchase;
	}
	public void setId_purchase(long id_purchase) {
		this.id_purchase = id_purchase;
	}
	public Date getRecording_date() {
		return recording_date;
	}
	public void setRecording_date(Date recording_date) {
		this.recording_date = recording_date;
	}
	public int getAvailableAmount() {
		return availableAmount;
	}
	public void setAvailableAmount(int availableAmount) {
		this.availableAmount = availableAmount;
	}
	
}
