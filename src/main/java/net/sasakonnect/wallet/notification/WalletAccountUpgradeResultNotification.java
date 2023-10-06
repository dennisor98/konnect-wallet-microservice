package net.sasakonnect.wallet.notification;

import java.util.List;

public class WalletAccountUpgradeResultNotification {
	private String userId;
	private String onboardingRequestId;
	private int status;
	private String accountId;
	private String accountType;
	private long completeTime;
	private List<String> rejectionReasonIds;
	private List<String> rejectionReasonMsgs;

}
