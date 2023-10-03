package net.sasakonnect.wallet.enums;

public enum WalletTransactionType {
	TTID0001("TTID0001"), // Withdraw To M-PESA
	TTID0002("TTID0002"), // Transfer Out
	TTID0003("TTID0003"), // Transfer In
	TTID0004("TTID0004"), // Interest Income
	TTID0005("TTID0005"), // M-PESA Pay Bill / Till
	TTID0006("TTID0006"), // Utility Payment
	TTID0007("TTID0007"); // Refund

	private final String value;

	WalletTransactionType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
