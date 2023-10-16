package net.sasakonnect.wallet.RequestDto;

public enum MpesaBillType {
	TILL(0), BUY_GOODS(1);

	int code;

	MpesaBillType(int i) {
		this.code = i;

		// TODO Auto-generated constructor stub
	}

	public int getCode() {
		return this.code;
	}
}
