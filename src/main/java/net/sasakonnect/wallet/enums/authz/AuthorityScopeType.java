package net.sasakonnect.wallet.enums.authz;

public enum AuthorityScopeType {
	PERSONAL("PERSONAL"), DEVICE("DEVICE"), PROFILE("PROFILE");

	private final String type;

	AuthorityScopeType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
