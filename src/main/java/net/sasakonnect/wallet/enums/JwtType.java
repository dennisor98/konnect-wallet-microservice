package net.sasakonnect.wallet.enums;

public enum JwtType {

	ACCESS_TOKEN("access_token"),
	WALLET_ADMIN_TOKEN("corporate_access_token"),
	WALLET_ADMIN_WINDOW_TOKEN("admin_window_token"),
	SME_TOKEN("sme_member_token"),
	REFRESH_TOKEN("refresh_token"),
	WALLET_CLIENT_TOKEN("wallet_client_token");

	String token;

	private JwtType(String token) {
		this.token = token;
	}

	public String getToken() {
		return this.token;
	}

}
