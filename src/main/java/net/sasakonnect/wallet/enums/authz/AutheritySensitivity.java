package net.sasakonnect.wallet.enums.authz;

public enum AutheritySensitivity {
	HIGH(1), MEDIUM(2), LOW(3);

	private final int level;

	AutheritySensitivity(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
