package com.jrrjw.mobilesafe.dao;

public class BlackNameInfo {
	public String phone;
	public String name;
	public String mode;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "BlackNameInfo [phone=" + phone + ", name=" + name + ", mode="
				+ mode + "]";
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
