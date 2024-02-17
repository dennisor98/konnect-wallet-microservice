package net.sasakonnect.wallet.enums;

public enum WalletTransactionType {
	TTID0001("TTID0001"), // Withdraw To M-PESA
	TTID0002("TTID0002"), // Transfer Out
	TTID0003("TTID0003"), // Transfer In
	TTID0004("TTID0004"), // Interest Income
	TTID0005("TTID0005"), // M-PESA Pay Bill / Till
	TTID0006("TTID0006"), // Utility Payment
	TTID0007("TTID0007"), // Refund
	TTID0008("TTID0008"), // FCY Deposit
	TTID0009("TTID0009"), // FCY Transfer Out
	TTID0010("TTID0010"), // FCY Exchange
	TTID0011("TTID0011");// Reversal

	private final String value;

	WalletTransactionType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
