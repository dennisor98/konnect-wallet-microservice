package net.sasakonnect.wallet.enums;

public enum EmploymentStatus {
	EMPLOYEE("A"), SELF_EMPLOYEE("B"), UNEMPLOYED("C"), EMPLOYER("D"), STUDENT("E"), OTHERS("F");

	private final String code;

	EmploymentStatus(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
