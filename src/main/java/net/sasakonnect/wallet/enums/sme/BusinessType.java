package net.sasakonnect.wallet.enums.sme;

public enum BusinessType {
	SOLE_PROPRIETORSHIP(1), LIMITED_LIABILITY_COMPANY(2), PARTNERSHIP(3);

	private final int code;

	BusinessType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
