package net.sasakonnect.wallet.notification;

import java.util.List;

import lombok.Data;

public class InternalBatchTransactionResultNotification {
	@Data
	public static class Params {
		private String orderId;
		private List<ResultArrayItem> resultArray;
	}

	@Data
	public static class ResultArrayItem {
		private String payeeAccountId;
		private String payeeAccountName;
		private int payeeType;
		private String currency;
		private String amount;
		private String txId;
		private int txStatus;
		private String failedReason;
		private long completedTime;
	}

}
