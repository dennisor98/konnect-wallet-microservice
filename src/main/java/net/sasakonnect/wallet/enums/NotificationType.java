package net.sasakonnect.wallet.enums;

public enum NotificationType {
	ONBOARD("0001"), TRANSACTION("0002"), BALANCE("0003"), INTERNAL_BATCH_TRANSACTION("0004"),
	WALLET_ACCOUNT_UPGRADE("0005"), SME_ACCOUNT_OPEN("0006"), UTILITY("0007"), BULK_PAYMENT("0008"),
	ACCOUNT_STATEMENT("0009"), FOREIGN_CURRENCY_DEPOSIT("0010"), FOREIGN_CURRENCY_OUTBOUND_TRANSACTION("0011"),
	MULTIPLE_ACCOUNT_OPENING("0012"), FOREIGN_CURRENCY_EXCHANGE("0013"), BULK_UTILITY_PAYMENT("0014");

	private final String code;

	NotificationType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
