package net.sasakonnect.wallet.enums;

public enum AirtimeProvider {
	SAFARICOM(0), AIRTEL(1), TELKOM(2);

	private final int code;

	AirtimeProvider(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
