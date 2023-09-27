package net.sasakonnect.wallet.enums;

public enum NotificationType {
	ONBOARD("0001"), TRANSACTION("0002"), BALANCE("0003"), INTERNAL_BATCH_TRANSACTION("0004"),
	WALLET_ACCOUNT_UPGRADE("0005"), SME_ACCOUNT_OPEN("0006"), UTILITY("0007"), BULK_PAYMENT("0008"),
	ACCOUNT_STATEMENT("0009");

	private final String code;

	NotificationType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
