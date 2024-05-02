package net.sasakonnect.wallet.enums;

public enum IdentityType {
   ID_NUMBER("ID_NUMBER"),MOBILE_NUMBER("MOBILENUMBER");
	
	
	private final String  value;
	
	IdentityType(String value)
	{
	  this.value = value;	
	}
	public String getValue() {
		return this.value;
	}
}
