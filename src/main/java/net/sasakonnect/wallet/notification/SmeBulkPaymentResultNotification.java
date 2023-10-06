package net.sasakonnect.wallet.notification;

import java.util.List;

import lombok.Data;

@Data
public class SmeBulkPaymentResultNotification {
	@Data
	public static class Params {
		private String bulkPaymentOrderId;
		private String payerAccountId;
		private String txType;
		private List<ResultArrayItem> resultArray;
	}

	@Data
	public static class ResultArrayItem {
		private String txId;
		private String benificiaryName;
		private String benificiaryAccount;
		private String benificiarySubAccount;
		private String benificiaryBankCode;
		private String paymentChannel;
		private String currency;
		private String amount;
		private String feeAmount;
		private String externalTxId;
		private int txStatus;
		private String errorCode;
		private String errorMsg;
		private String completedTime;
	}

}
