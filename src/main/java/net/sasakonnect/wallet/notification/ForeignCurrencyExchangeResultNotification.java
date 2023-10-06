package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class ForeignCurrencyExchangeResultNotification {
	private String applicationId;
	private String kesAccountId;
	private String fcAccountId;
	private String outTxId;
	private String inTxId;
	private String currency;
	private int status;
	private String amount;
	private String fxRate;
	private String operation;
	private String errorCode;
	private String errorMsg;
}
