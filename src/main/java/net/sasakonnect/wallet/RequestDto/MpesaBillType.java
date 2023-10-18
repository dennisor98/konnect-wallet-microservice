package net.sasakonnect.wallet.RequestDto;

public enum MpesaBillType {
	TILL(1), PAY_BILL(0);

	int code;

	MpesaBillType(int i) {
		this.code = i;

		// TODO Auto-generated constructor stub
	}

	public int getCode() {
		return this.code;
	}
}
