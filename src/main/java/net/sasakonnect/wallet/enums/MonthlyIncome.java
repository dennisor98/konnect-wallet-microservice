package net.sasakonnect.wallet.enums;

public enum MonthlyIncome {
	LESS_THAN_FIVE_THOUSAND("A"), FIVE_THOUSAND_TO_TEN_THOUSAND("B"), TEN_THOUSAND_TO_FIFTEEN_THOUSAND("C"),
	FIFTEEN_THOUSAND_TO_TWENTY_FIVE_THOUSAND("D"), TWENTY_FIVE_THOUSAND_AND_ABOVE("E");

	private final String code;

	MonthlyIncome(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
