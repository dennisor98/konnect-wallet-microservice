package net.sasakonnect.wallet.enums;

public enum PinResetQuestionaire {
	
	FULL_NAME("Full Name:"),ID_NUMBER("ID_NUMBER:"),DOB("Date of Birth:"),BALANCE("Account Balance:"),
	MOBILE_NUMBER("Phone Number:"),LAST_OUT_TRANSACTION("Last Transaction Amount out"),LAST_IN_TRANSACTION("Last amount received");
    private final String value;
	
	PinResetQuestionaire(String value){
		this.value =  value;
	}
	
	public String getValue() {
		return value;
	}
}
