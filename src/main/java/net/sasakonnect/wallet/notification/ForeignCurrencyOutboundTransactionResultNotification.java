package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class ForeignCurrencyOutboundTransactionResultNotification {
	private String applicationId;
	private String accountId;
	private String txId;
	private String refundTxId;
	private String operationBankCode;
	private String operationBankAccount;
	private String externalId;
	private int status;
	private String currency;
	private String amount;
	private String errorCode;
	private String errorMsg;

}
