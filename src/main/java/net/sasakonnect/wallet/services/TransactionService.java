package net.sasakonnect.wallet.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.TransactionResultNotification;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	WalletRepository walletRepository;
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;

	public Transaction saveTransaction(NotificationResult<TransactionResultNotification> results) {
		var trans = results.getParams();
		var existingTransaction = this.transactionRepository.findByTxId(trans.getTxId());
		if (!existingTransaction.isPresent()) {
			var transaction = Transaction.builder().txId(trans.getTxId()).txType(trans.getTxType())
					.externalTxId(trans.getExternalTxId()).accountId(trans.getAccountId())
					.accountName(trans.getAccountName()).oppoSubAccount(trans.getOppoSubAccount())
					// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
					.txStatus(trans.getTxStatus()).oppoAccountId(trans.getOppoAccountId())
					.oppoChannelId(trans.getOppoChannelId()).oppoAccountName(trans.getOppoAccountName())
					.thirdPartyTxType(trans.getThirdPartyTxType()).currency(trans.getCurrency())
					.amount(new BigDecimal(trans.getAmount())).build();
			return this.transactionRepository.save(transaction);
		} else {
//			var transaction = Transaction.builder().txId(trans.getTxId()).txType(trans.getTxType())
//					.externalTxId(trans.getExternalTxId()).accountId(trans.getAccountId())
//					.accountName(trans.getAccountName()).oppoSubAccount(trans.getOppoSubAccount())
//					// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
//					.oppoAccountName(trans.getAccountName()).thirdPartyTxType(trans.getThirdPartyTxType())
//					.currency(trans.getCurrency()).amount(new BigDecimal(trans.getAmount())).build();
//			this.transactionRepository.save(transaction);	
		}

		// results.getParams()results;
		// TODO Auto-generated method stub
		return null;

	}

	public List<Object> getFancanctialContact() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Wallet> wallets = this.walletRepository.findByUserWalletsUser(user);
		if (!wallets.isEmpty()) {
			var account = wallets.get(0).getAccountId();

			return this.transactionRepository.findDistinctInteractions(account).stream().map((Object[] element) -> {
				Map<String, Object> jsonMap = new HashMap<>();
				jsonMap.put("name", element.length > 0 ? element[0] : null);
				jsonMap.put("account", element.length > 1 ? element[1] : null);
				jsonMap.put("user_id", element.length > 2 ? element[2] : null);

				return jsonMap;
			}).filter(accounts -> !(accounts.get("name") == null || accounts.get("name") == account))
					.collect(Collectors.toList());

		}
		return List.of();

	}

	public Object getTrasactionStatus(String id) {
		var reqId = new HashMap<String, Object>();
		reqId.put("txId", id);
		var reqs = requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.GET_TRANSACTION_STATUS).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		return null;
	}

}
