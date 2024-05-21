package net.sasakonnect.wallet.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
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

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.Logs;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.enums.LogTypes;
import net.sasakonnect.wallet.enums.NotificationType;
import net.sasakonnect.wallet.enums.WalletTransactionType;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.TransactionResultNotification;
import net.sasakonnect.wallet.repository.LogsRepository;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;
@Slf4j
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
	
	@Autowired
	LogsRepository logsRepository;
	
	@Autowired
	UserService userService;

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
					.remarks(trans.getErrorMsg())
					.feeAmount(trans.getFeeAmount() != null ? new BigDecimal(trans.getFeeAmount()) : new BigDecimal(0))
					// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
					.txStatus(trans.getTxStatus())
					.oppoAccountId(trans.getOppoAccountId()).oppoChannelId(trans.getOppoChannelId())
					.oppoAccountName(trans.getOppoAccountName()).thirdPartyTxType(trans.getThirdPartyTxType())
					.counterpartyName(trans.getExtInfo().getCounterpartyName())
					.currency(trans.getCurrency()).amount(new BigDecimal(trans.getAmount())).build();
			User user = this.userService.findUserByAccountd(trans.getAccountId()).get();
			var log = Logs.builder()
					.description(user.getFirstName()+" "+user.getLastName()+"of acc No:"+trans.getAccountId()
					+" invoked a transaction with id"+trans.getTxId()+"of amount"+trans.getAmount() +"to" +"acc No:"+trans.getOppoAccountId())
					.activity(LogTypes.TRANSACTION)
					.build();
			this.logsRepository.save(log);
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
	
	public ResponseEntity<Object> searchTransaction(String queryString,Integer pageNumber,Integer pageSize){
		Page<Transaction> transactions = this.transactionRepository.searchTransaction(queryString,PageRequest.of(pageNumber,pageSize));
		Map<String,Object> data = new HashMap<>();
		if(!transactions.isEmpty()) {
			data.put("success", true);
			data.put("message","successful");
			data.put("totalRows", Double.valueOf(transactions.getTotalElements()));
			data.put("pageSize", transactions.getSize());
			data.put("currentPage", transactions.getNumber());
			data.put("nextPage", transactions.hasNext() ? transactions.nextPageable().getPageNumber() : null);
			data.put("hasNextPage", transactions.hasNext());
			data.put("hasPreviousPage", transactions.hasPrevious());
			data.put("transactions", transactions.get().collect(Collectors.toList()));
			
			return ResponseEntity.status(HttpStatus.OK).body(data);
		}else {
			data.put("success", false);
			data.put("message", "No matching records");
			data.put("transactions", new ArrayList<>());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		}
	}

	public Object getUserTransactionHistory(Integer pageNumber, Integer pageSize) {
		var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> data = new HashMap<>();

		List<Wallet> wallets = this.walletRepository.findByUserWalletsUser(user);
		if (!wallets.isEmpty()) {

			Page<Transaction> transactions = transactionRepository.findByUserAccountId(wallets.get(0).getAccountId(),
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
					resultData.put("counterPartyName",transaction.getCounterpartyName());
					resultData.put("feeAmount",transaction.getFeeAmount() == null ? "0" : transaction.getFeeAmount().toString());

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
	
	public ResponseEntity<Object> getWalletTransactionBehaviour(int year,int month){
		var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Wallet> wallet = this.walletRepository.findByUserWalletsUser(user);
		
		if(!wallet.isEmpty()) {
			List<Object[]> transactions = this.transactionRepository.findWalletTransactionBehaviour(wallet.get(0).getAccountId(),year,month);
			
			if(!transactions.isEmpty()) {
				
				var userTransactions = transactions.stream().map(transaction -> {
					Map<String,Object> map = new HashMap<>();
					 map.put("toMpesa",transaction[0]);
				     map.put("toWallet",transaction[1]);
				     map.put("received", transaction[2]);
				     map.put("toTillsAndPaybills", transaction[3]);
				     map.put("utilityPayments", transaction[4]);
				     return map;
				}).collect(Collectors.toList());
				Map<String,Object> map = new HashMap<>();
				map.put("success",true);
				map.put("message","Request successful");
				map.put("transactions", userTransactions.stream().toList());
				return ResponseEntity.status(HttpStatus.OK).body(map);
				
			}else {
				Map<String,Object> map = new HashMap<>();
				map.put("success",true);
				map.put("message","No transactions found");
				map.put("transactions",new ArrayList<>());
				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
			
		}
	 return null;	
	}
	
	public ResponseEntity<Object> getGeneralTransactionBehaviour(Integer year,Integer month){
			List<Object[]> transactions = null;
			if(year !=null && month == null) {
				transactions = this.transactionRepository.findWalletTransactionBehaviour(year);
			}
			if(year !=null && month !=null ) {
				System.out.println("Executing 2");
				transactions = this.transactionRepository.findWalletTransactionBehaviour(year,month);	
			}
			if(year ==null && month==null) {
				System.out.println("Executing 3");
				transactions = this.transactionRepository.findWalletTransactionBehaviour();
			}
			
			if(transactions !=null) {
				
				var userTransactions = transactions.stream().map(transaction -> {
					Map<String,Object> map = new HashMap<>();
					 map.put("toMpesaAmount",transaction[0]);
					 map.put("toMpesaCount",transaction[1]);
				     map.put("toWalletAmount",transaction[2]);
				     map.put("toWalletCount", transaction[3]);
				     map.put("toTillsAndPaybillsAmount", transaction[4]);
				     map.put("toTillsAndPaybillsCount", transaction[5]);				    
				     map.put("utilityAmount", transaction[6]);
				     map.put("utilityCount", transaction[7]);
				     return map;
				}).collect(Collectors.toList());
				Map<String,Object> map = new HashMap<>();
				map.put("success",true);
				map.put("message","Request successful");
				map.put("transactions", userTransactions.stream().toList());
				return ResponseEntity.status(HttpStatus.OK).body(map);
				
			}else {
				Map<String,Object> map = new HashMap<>();
				map.put("success",true);
				map.put("message","No transactions found");
				map.put("transactions",new ArrayList<>());
				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
			
		
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

		} else if (transaction.getNotificationType().equalsIgnoreCase(NotificationType.BALANCE.getCode())
				&& (transaction.getParams().getTxType().equalsIgnoreCase(WalletTransactionType.TTID0005.getValue()))) {
			return false;
		}
		return true;

	}
	
	public ResponseEntity<Object> getRecentTransactionContact(String accountId,String transactionType,Integer pageNumber,Integer pageSize) {
		Page<Transaction> recentTransactions = transactionType.equalsIgnoreCase("wallet") ? this.transactionRepository.findWalletToWalletTransContact("TTID0002",accountId,PageRequest.of(pageNumber,pageSize)): this.transactionRepository.findRecentTransactionContactByTxType(transactionType, accountId,PageRequest.of(pageNumber,pageSize));
	   Map<String,Object> payload = new HashMap<>();
		if(!recentTransactions.isEmpty()) {
		 var rt =  recentTransactions.stream().map(t->{
			 Map<String,Object> map = new HashMap<>();
			 map.put("accountId",t.getOppoAccountId());
			 map.put("accountName",t.getOppoAccountName() !=null ? t.getOppoAccountName() : t.getCounterpartyName());
			 map.put("subAccountId",t.getOppoSubAccount());		
			 return map;
		   }).collect(Collectors.toList());
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",true);
		   map.put("message","Request completed");
		   map.put("contacts",rt.size() > 0 ? rt : new ArrayList<>());
		   map.put("pageSize", recentTransactions.getSize());
		   map.put("currentPage", recentTransactions.getNumber());
		   map.put("nextPage", recentTransactions.hasNext() ? recentTransactions.nextPageable().getPageNumber() : null);
		   map.put("hasNextPage", recentTransactions.hasNext());
		   map.put("hasPreviousPage", recentTransactions.hasPrevious());
		   payload.put("payload", map);

		return ResponseEntity.status(HttpStatus.OK).body(payload);
	   }else {
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",true);
		   map.put("message","Request completed");
		   map.put("contacts",new ArrayList<>());
		   payload.put("payload", map);
		   return ResponseEntity.status(HttpStatus.OK).body(payload);
	   }
	   
	}
	
	public List<Transaction> getAllTransactions(ChannelType channel,Date startDate,Date endDate) {
		List<Transaction> transactions = null;
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        String formattedStartDateString = sdf.format(startDate);
	        String formattedEndDateString = sdf.format(endDate);

	        // Convert formatted string to java.sql.Date
	        java.sql.Date formattedStartDate = java.sql.Date.valueOf(formattedStartDateString);
	        java.sql.Date formattedEndDate = java.sql.Date.valueOf(formattedEndDateString);
		if(channel.getValue().equalsIgnoreCase(ChannelType.MPESA_ACCOUNT.toString())) {
			 transactions = this.transactionRepository.findMpesaTransactions(formattedStartDate,formattedEndDate,8,"M-PESA");
		}
		
		if(channel.getValue().equalsIgnoreCase(ChannelType.MPESA_PAYBILL.toString())) {
			transactions = this.transactionRepository.findPayBillTransactions(formattedStartDate,formattedEndDate);
		}
		
		if(channel.getValue().equalsIgnoreCase(ChannelType.MPESA_TILL.toString())) {
			transactions = this.transactionRepository.findTillTransactions(formattedStartDate,formattedEndDate);
		}
		
		if(channel.getValue().equalsIgnoreCase(ChannelType.WALLET.toString())) {
			transactions = this.transactionRepository.findWalletTransactions(formattedStartDate,formattedEndDate);
		}
		
		if(channel.getValue().equalsIgnoreCase(ChannelType.PESA_LINK.toString())) {
			transactions = this.transactionRepository.findPesalinkTransactions(formattedStartDate,formattedEndDate);
		}
		if(transactions !=null) {
			return transactions;
		}
		return new ArrayList<>();
	}
	
	public Optional<Transaction> getLastInTransaction(String accountId){
		return this.transactionRepository.findWalletLatestInTransaction(accountId);
	}
		
	public Optional<Transaction> getLastOutTransaction(String accountId){
		return this.transactionRepository.findWalletLatestOutTransaction(accountId);
	}
	
	
	
	

}
