package net.sasakonnect.wallet.enums;

public enum OnboardingStatusType {
	NOT_SUBMIT(0),
    SUBMITTED(1),
    PROCESSING(2),
    PASSED(3),
    REJECTED(4),
    ACCOUNT_CLOSED(5),
    WAITING_FOR_ACCOUNT_OPENING(6),
    ACCOUNT_OPENED(7),
    FAILED_TO_OPEN_ACCOUNT(8),
    MANUAL_REVIEWING(9);

    private final int code;

    OnboardingStatusType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
	public String toString() {
		return String.valueOf(this.getCode());
	}
}
