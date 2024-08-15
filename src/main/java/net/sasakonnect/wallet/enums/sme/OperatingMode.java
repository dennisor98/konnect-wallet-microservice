package net.sasakonnect.wallet.enums.sme;

public enum OperatingMode {
	SINGLY(1), EITHER_TO_SIGN(2), ANY_TWO_TO_SIGN(3), ALL_OF_US_JOINTLY(4), OTHER(5);

	private final int value;

	OperatingMode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static OperatingMode fromValue(int value) {
		for (OperatingMode mode : OperatingMode.values()) {
			if (mode.getValue() == value) {
				return mode;
			}
		}
		throw new IllegalArgumentException("Invalid OperatingMode value: " + value);
	}
	
	public static int getCodeByName(String name) {
        try {
            return OperatingMode.valueOf(name).getValue();
        } catch (IllegalArgumentException e) {
            // Handle the case where the name does not match any enum constant
            throw new IllegalArgumentException("No enum constant with name " + name);
        }
    }
}
