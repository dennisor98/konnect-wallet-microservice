package net.sasakonnect.wallet.enums;

public enum PayType {
	PAYBILL(0),TILL(1);

	private final int code;
	PayType(int code){ 
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
