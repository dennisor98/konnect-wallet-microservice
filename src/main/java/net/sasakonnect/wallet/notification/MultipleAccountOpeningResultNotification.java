package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class MultipleAccountOpeningResultNotification {
	private String applicationId;
	private String accountId;
	private String accountName;
	private String currency;
	private int status;
	private String errorCode;

}
