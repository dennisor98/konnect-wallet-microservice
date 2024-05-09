package net.sasakonnect.wallet.enums;

public enum PinResetType {
	 FORGOT_PIN("FORGOT"),BLOCKED_PIN("BLOCKED");
	
	
		private final String  value;
		
		PinResetType(String value)
		{
		  this.value = value;	
		}
		public String getValue() {
			return this.value;
		}
}
