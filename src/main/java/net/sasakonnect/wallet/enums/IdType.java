package net.sasakonnect.wallet.enums;

public enum IdType {
	KENYA_ID, ALIEN_KENYA_ID, PASSPORT;

	public int evaluateId() {
		switch (this) {
		case KENYA_ID:
			return 101;
		case ALIEN_KENYA_ID:
			return 102;
		case PASSPORT:
			return 103;
		default:
			return 101;// default case to handle unexpected values
		}

	}

}
