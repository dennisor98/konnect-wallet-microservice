package net.sasakonnect.wallet.enums;

public enum Gender {
	MALE(1), FEMALE(0);

	int gender;

	Gender(int i) {
		this.gender = i;
		// TODO Auto-generated constructor stub
	}

	public int getValue() {
		return this.gender;
	}
}
