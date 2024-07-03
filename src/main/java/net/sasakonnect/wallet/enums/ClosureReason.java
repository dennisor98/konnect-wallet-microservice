package net.sasakonnect.wallet.enums;

public enum ClosureReason {
	NO_LONGER_USING_ACCOUNT("I'm no longer using the account"),
	DISSATISFIED_BANK_SERVICE("Bank service - Dissatisfied with some aspects of choice's service"),
	DISSATISFIED_PLATFORM(
			"Platform - Dissatisfied with some aspects of the software platforms, i.e. User experience, system usability"),
	DISSATISFIED_PRICING("Pricing - Dissatisfied with interest or charges on my account"),
	DISSATISFIED_POLICY(
			"Policy - Dissatisfied with the rules around the product, i.e. Onboarding, notice period, withdrawal policy"),
	MOVING_AWAY("Moving away"), CIRCUMSTANCES_CHANGED("Account holder circumstances have changed"),
	USING_FUNDS("Using funds"),
	SIMILAR_ACCOUNT_HELD("Similar account already held or opening a similar account with choice"),
	OPENED_IN_ERROR("Opened in error");

	private final String description;

	ClosureReason(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
