package net.sasakonnect.wallet.enums;

public enum TransactionStatus {
	TIMEOUT(-1), PENDING(1), PROCESSING(2), FAILED(4), SUCCESS(8);

	private final int value;

	TransactionStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(this.getValue());
	}
}
