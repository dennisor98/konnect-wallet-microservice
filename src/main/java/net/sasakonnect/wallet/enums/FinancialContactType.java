package net.sasakonnect.wallet.enums;

public enum FinancialContactType {
   MPESA("MPESA"),
   PESA_LINK("PESA_LINK"),
   PAYBILL("PAYBILL"),
   TILL("TILL"),
   WALLET("WALLET"),
   UTILITY("UTILITY");
   
   private final String value;

	FinancialContactType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

