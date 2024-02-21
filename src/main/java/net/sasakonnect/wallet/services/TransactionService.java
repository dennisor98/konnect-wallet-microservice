package net.sasakonnect.wallet.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.enums.NotificationType;
import net.sasakonnect.wallet.enums.WalletTransactionType;
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
					.balance(trans.getBalance() == null ? null : new BigDecimal(trans.getBalance()))
					.oppoBankCode(trans.getOppoBankCode()).requestId(results.getRequestId())
					// .extInfo(trans.getExtInfo().toString())
					.notificationType(trans.getTxType())
					.feeAmount(trans.getFeeAmount() != null ? new BigDecimal(trans.getFeeAmount()) : new BigDecimal(0))
					// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
					.txStatus(trans.getTxStatus())

					.oppoAccountId(trans.getOppoAccountId()).oppoChannelId(trans.getOppoChannelId())
					.oppoAccountName(trans.getOppoAccountName()).thirdPartyTxType(trans.getThirdPartyTxType())
					.currency(trans.getCurrency()).amount(new BigDecimal(trans.getAmount())).build();
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

	public Object getUserTransactionHistory(Integer pageNumber, Integer pageSize) {
		var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> data = new HashMap<>();

		List<Wallet> wallets = this.walletRepository.findByUserWalletsUser(user);
		if (!wallets.isEmpty()) {

			Page<Transaction> transactions = transactionRepository.findByAccountId(wallets.get(0).getAccountId(),
					PageRequest.of(pageNumber, pageSize));
			Map<String, Object> payload = new HashMap<>();
			payload.put("code", "");
			payload.put("msg", "Request succesfull");
			payload.put("sender", "apigw.baas.konnect");
			payload.put("requestId", UUID.randomUUID().toString());
			payload.put("locale", "en_KE");

			// Map<Object> result = new ArrayList<Object>();
			if (!transactions.isEmpty()) {
				List<Map<String, Object>> transactionCollection = transactions.get().map(transaction -> {
					Map<String, Object> resultData = new HashMap<>();
					resultData.put("txId", transaction.getTxId());
					resultData.put("txType", transaction.getTxType());
					resultData.put("accountId", transaction.getAccountId());
					resultData.put("oppoBankCode", transaction.getOppoBankCode());
					resultData.put("oppoBankName", transaction.getOppoAccountName());
					resultData.put("oppoAccountId", transaction.getOppoAccountId());
					resultData.put("oppoAccountName", transaction.getOppoAccountName());
					resultData.put("thirdPartyTxType", transaction.getThirdPartyTxType());
					resultData.put("currency", transaction.getCurrency());
					resultData.put("amount", transaction.getAmount().toString());
					resultData.put("txStatus", Double.valueOf(transaction.getTxStatus().toString()));
					resultData.put("createTime", Double.valueOf(transaction.getCreatedAt().getTime()));
					resultData.put("updateTime", Double.valueOf(transaction.getUpdatedAt().getTime()));
					resultData.put("feeAmount",
							transaction.getFeeAmount() == null ? "0" : transaction.getFeeAmount().toString());

					return resultData;
				}).collect(Collectors.toList());
				data.put("result", transactionCollection);

//			   transactions.forEach();
			} else {
				data.put("result", new ArrayList());

			}
//		   resultData.put("txId", transactions.)

			data.put("totalRows", Double.valueOf(transactions.getTotalElements()));
			data.put("pageSize", transactions.getSize());
			data.put("currentPage", transactions.getNumber());
			data.put("nextPage", transactions.hasNext() ? transactions.nextPageable().getPageNumber() : null);
			data.put("hasNextPage", transactions.hasNext());
			data.put("hasPreviousPage", transactions.hasPrevious());
			payload.put("data", data);
			return payload;
		}

		return null;
	}

	public Object getTransactionHistory(Integer pageNumber, Integer pageSize) {

		var transactions = transactionRepository.findAll(PageRequest.of(pageNumber, pageSize));
//		transactions.nextPageable().
		// var transactionsPayload:TransactionHistory =
//		Integer pageSize;
//		   Integer currentPage;
//		   Integer nextPage;
//		   Boolean hasNextPage;
//		   Boolean hasPreviousPage;
		Map<String, Object> transactionsMap = new HashMap<String, Object>();
		transactionsMap.put("transactions", transactions.get().collect(Collectors.toList()));
		transactionsMap.put("pageSize", transactions.getSize());
		transactionsMap.put("currentPage", transactions.getNumber());
		transactionsMap.put("nextPage", transactions.hasNext() ? transactions.nextPageable().getPageNumber() : null);
		transactionsMap.put("hasNextPage", transactions.hasNext());
		transactionsMap.put("hasPreviousPage", transactions.hasPrevious());
		transactionsMap.put("totalItems", transactions.getTotalElements());
		return transactionsMap;

	}

	public Object getTransactionHistoryByAccountNumber(String accountNumber, Integer pageNumber, Integer pageSize) {

		var transactions = transactionRepository.findByAccountId(accountNumber,
				PageRequest.of(pageNumber == null ? 0 : pageNumber, pageSize == null ? 100 : pageSize));
//		transactions.nextPageable().
		// var transactionsPayload:TransactionHistory =
//		Integer pageSize;
//		   Integer currentPage;
//		   Integer nextPage;
//		   Boolean hasNextPage;
//		   Boolean hasPreviousPage;
		Map<String, Object> transactionsMap = new HashMap<>();
		transactionsMap.put("transactions", transactions.get().collect(Collectors.toList()));
		transactionsMap.put("pageSize", transactions.getSize());
		transactionsMap.put("currentPage", transactions.getNumber());
		transactionsMap.put("nextPage", transactions.hasNext() ? transactions.nextPageable().getPageNumber() : null);
		transactionsMap.put("hasNextPage", transactions.hasNext());
		transactionsMap.put("hasPreviousPage", transactions.hasPrevious());
		transactionsMap.put("totalItems", transactions.getTotalElements());
		return transactionsMap;

	}

	public ResponseEntity<Object> getWalletTransactionBreakdown() {
		var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Wallet> wallet = this.walletRepository.findByUserWalletsUser(user);
		Map<String, Object> transMap = new HashMap<>();
		if (!wallet.isEmpty()) {
			Long totalTransactions = this.transactionRepository.findCountByAccountId(wallet.get(0).getAccountId());
			Long totalReceived = this.transactionRepository.findAllReceived(wallet.get(0).getAccountId());
			Long totalSent = this.transactionRepository.findAllSent(wallet.get(0).getAccountId());
			Map<String, Object> map = new HashMap<>();
			map.put("sent", totalSent);
			map.put("received", totalReceived);
			map.put("totalTransactions", totalTransactions);
			transMap.put("transactions", map);
		} else {
			transMap.put("transactions", new ArrayList<Object>());
		}

		return ResponseEntity.status(HttpStatus.OK).body(transMap);
	}

	public ResponseEntity<Object> getWalletTransactionRanks(Integer pageNumber, Integer pageSize) {
		Map<String, Object> map = new HashMap<>();
		var transactionRank = this.transactionRepository.findWalletRank(pageNumber, pageSize);
		if (!transactionRank.isEmpty()) {
			var rank = transactionRank.stream().map(r -> {
				Map<String, Object> tmap = new HashMap<>();
				tmap.put("account_name", r[0]);
				tmap.put("account_id", r[1]);
				tmap.put("total_transactions", r[2]);
				tmap.put("account_amount", r[3]);
				return tmap;
			}).collect(Collectors.toList());
			map.put("success", true);
			map.put("message", "Request successful");
			map.put("wallet_rank", rank);
		}
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	public ResponseEntity<Object> getDailyTransactionTrend(Integer year, Integer month) {
		List<Object[]> trends = this.transactionRepository.findTransactionTrend(month, year);
		Map<String, Object> resmap = new HashMap<>();
		if (trends.isEmpty()) {
			resmap.put("success", true);
			resmap.put("transaction_trends", trends);
			return ResponseEntity.status(HttpStatus.OK).body(resmap);
		} else {
			var trendMap = trends.stream().map(t -> {
				Map<String, Object> map = new HashMap<>();
				map.put("date", t[0]);
				map.put("total_transactions", t[1]);
				map.put("total_transacted", t[2]);
				return map;
			}).collect(Collectors.toList());
			resmap.put("success", true);
			resmap.put("transaction_trends", trendMap);
			return ResponseEntity.status(HttpStatus.OK).body(resmap);
		}

	}

	public ResponseEntity<Object> getMonthlyTransactionTrend(Integer year) {
		List<Object[]> trends = this.transactionRepository.findMonthlyTransactionTrend(year);
		Map<String, Object> resmap = new HashMap<>();
		if (trends.isEmpty()) {
			resmap.put("success", true);
			resmap.put("transaction_trends", trends);
			return ResponseEntity.status(HttpStatus.OK).body(resmap);
		} else {
			var trendMap = trends.stream().map(t -> {
				Map<String, Object> map = new HashMap<>();
				map.put("month", t[0]);
				map.put("total_transactions", t[1]);
				map.put("total_transacted", t[2]);
				return map;
			}).collect(Collectors.toList());
			resmap.put("success", true);
			resmap.put("transaction_trends", trendMap);
			return ResponseEntity.status(HttpStatus.OK).body(resmap);
		}

	}

	public ResponseEntity<Object> getAnnualTransactionTrend() {
		List<Object[]> trends = this.transactionRepository.findAnnualTransactionTrend();
		Map<String, Object> resmap = new HashMap<>();
		if (trends.isEmpty()) {
			resmap.put("success", true);
			resmap.put("transaction_trends", trends);
			return ResponseEntity.status(HttpStatus.OK).body(resmap);
		} else {
			var trendMap = trends.stream().map(t -> {
				Map<String, Object> map = new HashMap<>();
				map.put("year", t[0]);
				map.put("total_transactions", t[1]);
				map.put("total_transacted", t[2]);
				return map;
			}).collect(Collectors.toList());
			resmap.put("success", true);
			resmap.put("transaction_trends", trendMap);
			return ResponseEntity.status(HttpStatus.OK).body(resmap);
		}

	}

	public Optional<Transaction> getTransactionById(String txId) {
		return this.transactionRepository.findTransactionByTxtId(txId);
		// TODO Auto-generated method stub

	}

	public Boolean isUpdatableTransaction(NotificationResult<TransactionResultNotification> transaction) {
		if (transaction.getNotificationType().equalsIgnoreCase(NotificationType.BALANCE.getCode())
				&& (transaction.getParams().getTxType().equalsIgnoreCase(WalletTransactionType.TTID0006.getValue()))) {
			return false;

		}
		return true;

	}

}
