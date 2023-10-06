package net.sasakonnect.wallet.enums;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationBody {
	private int status;
	private String userId;
	private String accountId;
	private String accountType;
	private long completeTime;
	private List<Integer> rejectionReasonIds;
	private String onboardingRequestId;
	private List<String> rejectionReasonMsgs;

}
