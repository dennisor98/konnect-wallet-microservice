package net.sasakonnect.wallet.config;

public enum KonnectHeader {
	X_TRANSACTION_HEADER("x-transaction-id");

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