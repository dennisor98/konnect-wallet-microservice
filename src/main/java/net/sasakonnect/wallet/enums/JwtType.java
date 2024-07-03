package net.sasakonnect.wallet.enums;

public enum JwtType {

	ACCESS_TOKEN("access_token"),SME_TOKEN("sme_member_token"), REFRESH_TOKEN("refresh_token");

	String token;

	private JwtType(String token) {
		this.token = token;
	}

	public String getToken() {
		return this.token;
	}

}
