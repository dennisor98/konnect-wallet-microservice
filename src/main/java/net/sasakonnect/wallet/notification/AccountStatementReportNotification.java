package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class AccountStatementReportNotification {
	private String jobId;
	private String statementUrl;
}
