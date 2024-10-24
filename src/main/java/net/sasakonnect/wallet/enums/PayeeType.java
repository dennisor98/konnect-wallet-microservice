package net.sasakonnect.wallet.enums;

import lombok.ToString;

@ToString
public enum PayeeType {
	INDIVIDUAL(0),BUSINESS(1);

	private final int code;
	PayeeType(int code){ 
		this.code =  code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return String.valueOf(this.getCode());
	}
}
