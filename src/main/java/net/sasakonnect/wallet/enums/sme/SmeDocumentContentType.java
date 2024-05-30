package net.sasakonnect.wallet.enums.sme;

public enum SmeDocumentContentType {
	PDF("pdf"), IMAGE("image");

	private final String value;

	SmeDocumentContentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static SmeDocumentContentType fromValue(String value) {
		for (SmeDocumentContentType mode : SmeDocumentContentType.values()) {
			if (mode.getValue().equalsIgnoreCase(value)) {
				return mode;
			}
		}
		throw new IllegalArgumentException("Invalid OperatingMode value: " + value);
	}

}
