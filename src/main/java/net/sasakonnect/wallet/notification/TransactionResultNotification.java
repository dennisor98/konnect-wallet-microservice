package net.sasakonnect.wallet.notification;

import lombok.Data;
import net.sasakonnect.wallet.domain.ExtInfo;

@Data
public class TransactionResultNotification {
	private String txId;
	private String txType;
	private String externalTxId;
	private String accountId;
	private String accountName;
	private String accountType; // Updated to String
	private String oppoChannelId;
	private String oppoSubAccount;
	private String oppoBankCode;
	private int mpesaBusinessPayType; // Updated to int
	private String oppoAccountId;
	private String oppoAccountName;
	private String thirdPartyTxType;
	private String currency;
	private String amount; // Updated to String
	private String feeAmount;
	private String balance;
	private int txStatus; // Updated to int
	private long createTime; // Updated to long
	private long updateTime; // Updated to long
	private String errorCode;
	private String errorMsg;
	private ExtInfo extInfo;
}



