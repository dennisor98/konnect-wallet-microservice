package net.sasakonnect.wallet.RequestDto;

public enum UtilityBillType {
	DSTV(0), KWESE(2), GOTV(1), STARTIMES(3), WATER(6);

	int code;

	UtilityBillType(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

}
