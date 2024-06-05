package net.sasakonnect.wallet.notification;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeAccountOpeningResultNotification {
	private String userId;
	private String onboardingRequestId;
	private int status;
	private String accountId;
	private String accountType;
	private long completeTime;
	private List<Object> rejectionReasonIds;
	private List<Object> rejectionReasonMsgs;

}
