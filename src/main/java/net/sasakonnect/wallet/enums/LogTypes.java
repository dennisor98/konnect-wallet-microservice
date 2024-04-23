package net.sasakonnect.wallet.enums;

public enum LogTypes {
	TRANSACTION("TRANSACTION"), LOGIN("LOGIN"),PIN_SET("PIN_SET"),PIN_RESET("PIN_RESET"),PIN_UPDATE("PIN_UPDATE"),
	STATEMENT_REQUEST("STATEMENT_REQUEST"),INVOICE_REQUEST("INVOICE_REQUEST");

	private final String code;

	LogTypes(String code) {
		this.code = code;
	}

	public String getValue() {
		return code;
	}
}
