package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class BulkUtilityPaymentNotification {

	private static class Params {
		private String bulkPaymentOrderId;
		private String payerAccountId;
		private int txCount;
		private Result[] resultArray;
	}

	private static class Result {
		private String paymentId;
		private String payServiceCategory;
		private String subType;
		private String orderNo;
		private String txId;
		private String refundTxId;
		private String amount;
		private String status;
		private String errorCode;
		private String errorMsg;
	}

}
