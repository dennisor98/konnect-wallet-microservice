package net.sasakonnect.wallet.enums;

public enum NotificationTargetType {
    INDIVIDUAL("INDIVIDUAL"),
    GENERAL("GENERAL");
	private String value;
	NotificationTargetType(String value){
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
