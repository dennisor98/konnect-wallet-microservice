package net.sasakonnect.wallet.enums;

public enum ClosureReason {
	NO_LONGER_USING_ACCOUNT(1), DISSATISFIED_BANK_SERVICE(2), DISSATISFIED_PLATFORM(3), DISSATISFIED_PRICING(4),
	DISSATISFIED_POLICY(5), MOVING_AWAY(6), CIRCUMSTANCES_CHANGED(7), USING_FUNDS(8), SIMILAR_ACCOUNT_HELD(9),
	OPENED_IN_ERROR(10);

	private final int description;

	ClosureReason(int description) {
		this.description = description;
	}

	public int getDescription() {
		return description;
	}

}
