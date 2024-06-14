package net.sasakonnect.wallet.config;

public enum KonnectHeader {
	X_TRANSACTION_HEADER("x-transaction-id"), CLIENT_APP_KEY_HEADER("app-key"), SECRET_APP_KEY_HEADER("secret-key"),
	REFRESH_TOKEN_HEADER("refresh-token-header"), KONNECT_APP_VERSION("app-version-number");

	private String value;

	KonnectHeader(String string) {
		this.value = string;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return this.value;
	}
}