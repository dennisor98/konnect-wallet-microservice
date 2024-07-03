package net.sasakonnect.wallet.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountClosureNotification {
	private String requestId; // Account closing request ID
	private String accountId; // Account number
	private String completeTime; // Completion time
	private String status; // Status (1: account closed, 2: request rejected)
	private String rejectionReason;// Rejection reason

}
