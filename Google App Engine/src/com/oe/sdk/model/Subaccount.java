package com.oe.sdk.model;

import com.oe.sdk.util.Str;

public class Subaccount {
	public static final int HAS_CREDIT = 0;
	public static final int USE_SUPER_CREDIT = 1;
	public static final int USE_BOTH_CREDITS = 2;
	private int credit_mode;
	private String company_name;
	private String fiscal_code;
	private String vat_number;
	private String name;
	private String surname;
	private String email;
	private String address;
	private String city;
	private String province;
	private String zip;
	private String mobile;
	private String login;
	private String password;
	private boolean active;
	private SUBACCOUNT_TYPE subaccount_type = DEFAULT_SUBACCOUNT_TYPE;

	public int getCredit_mode() {
		return this.credit_mode;
	}
	public void setCredit_mode(int credit_mode) {
		this.credit_mode = credit_mode;
	}
	public void setSubaccountHasCredits() {
		this.credit_mode = HAS_CREDIT;
	}
	public void setSubaccountUseSuperaccountCredit() {
		this.credit_mode = USE_SUPER_CREDIT;
	}
	public void setSubaccountUseBothCredits() {
		this.credit_mode = USE_BOTH_CREDITS;
	}
	public String getCompany_name() {
		return this.company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getFiscal_code() {
		return this.fiscal_code;
	}
	public void setFiscal_code(String fiscal_code) {
		this.fiscal_code = fiscal_code;
	}
	public String getVat_number() {
		return this.vat_number;
	}
	public void setVat_number(String vat_number) {
		this.vat_number = vat_number;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return this.surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return this.address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return this.province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getZip() {
		return this.zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getMobile() {
		return this.mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLogin() {
		return this.login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isActive() {
		return this.active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public SUBACCOUNT_TYPE getSubaccountType() {
		return this.subaccount_type;
	}
	public void setSubaccountType(SUBACCOUNT_TYPE subaccount_type) {
		if (subaccount_type == null)
			return;
		this.subaccount_type = subaccount_type;
	}
	public String getStr_subaccountType() {
		return this.subaccount_type.name().toLowerCase();
	}
	public void setStr_subaccountType(String subaccount_type) {
		if (Str.isEmpty(subaccount_type))
			return;
		try {
			this.subaccount_type = SUBACCOUNT_TYPE.valueOf(subaccount_type.toUpperCase());
		} catch (java.lang.IllegalArgumentException iae) {
			this.subaccount_type = DEFAULT_SUBACCOUNT_TYPE;
		}
	}

	public enum SUBACCOUNT_TYPE { COMPANY,PRIVATE }

	private static final SUBACCOUNT_TYPE DEFAULT_SUBACCOUNT_TYPE = SUBACCOUNT_TYPE.COMPANY;
}
