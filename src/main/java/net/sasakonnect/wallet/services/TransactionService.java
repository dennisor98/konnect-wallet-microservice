package net.sasakonnect.wallet.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.TransactionResultNotification;
import net.sasakonnect.wallet.repository.TransactionRepository;

public class TransactionService {
	@Autowired
	TransactionRepository transactionRepository;

	public void saveTransaction(NotificationResult<TransactionResultNotification> results) {
		var trans = results.getParams();
		var transaction = Transaction.builder().txId(trans.getTxId()).txType(trans.getTxType())
				.externalTxId(trans.getExternalTxId()).accountId(trans.getAccountId())
				.accountName(trans.getAccountName()).oppoSubAccount(trans.getOppoSubAccount())
				// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
				.oppoAccountName(trans.getAccountName()).thirdPartyTxType(trans.getThirdPartyTxType())
				.currency(trans.getCurrency()).amount(new BigDecimal(trans.getAmount())).build();
		this.transactionRepository.save(transaction);

		// results.getParams()results;
		// TODO Auto-generated method stub

	}

}
