package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class BalanceChangeNotification {
	private String txId;
	private String externalTxId;
	private String userId;
	private String accountId;
	private String accountName;
	private String txType;
	private String oppoBankCode;
	private String oppoAccountId;
	private String oppoAccountName;
	private String oppoSubAccount;
	private String thirdPartyTxType;
	private int mpesaBusinessPayType; // Updated to int
	private String currency;
	private String amount;
	private String feeAmount;
	private String balance;
	private long completeTime; // Updated to long
	private ExtInfo extInfo;

	@Data
	public static class ExtInfo {
		private String transactionNarrative;
		private String oppoPhoneNumber;
	}
}
