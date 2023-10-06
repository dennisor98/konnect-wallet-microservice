package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class PersonalOrboardingResultNotification {

	private String userId;
	private String onboardingRequestId;
	private int status;
	private String accountId;
	private String accountType;
	private Long completeTime;
	private String rejectionReasonIds;
	private String rejectionReasonMsgs;

}
