package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class ForeignCurrencyDepositResultNotification {
	private String applicationId;
	private String accountId;
	private String txId;
	private int status;
	private String currency;
	private String amount;
	private String errorCode;
	private String errorMsg;

}
