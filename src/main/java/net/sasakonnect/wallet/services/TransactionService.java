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
import org.springframework.data.domain.Pageable;
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
	
	
	public Object getUserTransactionHistory(Integer pageSize,Integer pageNumber) {
		var user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		List<Wallet> wallets = this.walletRepository.findByUserWalletsUser(user);
		if(!wallets.isEmpty()) {
			
			Page<Transaction> transactions =  transactionRepository.findByAccountId(wallets.get(0).getAccountId(),PageRequest.of(pageSize,pageNumber));
			Map<String,Object> payload =  new HashMap<>();
			payload.put("code", "");
			payload.put("msg","Request succesfull");
			payload.put("sender", "apigw.baas.konnect");
			payload.put("requestId",UUID.randomUUID().toString());
			payload.put("locale","en_KE");
			
		   Map<String,Object> resultData = new HashMap<>();
		   if(!transactions.isEmpty()) {
			   transactions.forEach(transaction -> {
				   resultData.put("txId",transaction.getExternalTxId());
				   resultData.put("txType",transaction.getTxType());
				   resultData.put("accountId",transaction.getAccountId());
				   resultData.put("oppoBankCode",transaction.getOppoBankCode());
				   resultData.put("oppoBankName",transaction.getOppoAccountName());
				   resultData.put("oppoAccountId",transaction.getOppoAccountId());
				   resultData.put("oppoAccountName",transaction.getOppoAccountName());
				   resultData.put("thirdPartyTxType",transaction.getThirdPartyTxType());
				   resultData.put("currency", transaction.getCurrency());
				   resultData.put("amount",transaction.getAmount());
				   resultData.put("txStatus",transaction.getTxStatus());
				   resultData.put("createTime",transaction.getCreatedAt().getTime());
				   resultData.put("updateTime",transaction.getUpdatedAt().getTime());
				   resultData.put("feeAmount",transaction.getFeeAmount());
				   
			   });
		   }else {
			   resultData.put("txId",null);
			   resultData.put("txType",null);
			   resultData.put("accountId",null);
			   resultData.put("oppoBankCode",null);
			   resultData.put("oppoBankName",null);
			   resultData.put("oppoAccountId",null);
			   resultData.put("oppoAccountName",null);
			   resultData.put("thirdPartyTxType",null);
			   resultData.put("currency",null);
			   resultData.put("amount",null);
			   resultData.put("txStatus",null);
			   resultData.put("createTime",null);
			   resultData.put("updateTime",null);
			   resultData.put("feeAmount",null);
		   }
//		   resultData.put("txId", transactions.)
		   List<Object> result = new ArrayList<Object>(); 
			result.add(resultData);
			Map<String,Object> data =  new HashMap<>();
			data.put("result",result);
			data.put("totalRows",transactions.getTotalElements());
			data.put("pageSize", transactions.getSize());
			data.put("currentPage",transactions.getNumber());
			data.put("nextPage",transactions.hasNext()?transactions.nextPageable().getPageNumber() : null);
			data.put("hasNextPage",transactions.hasNext());
			data.put("hasPreviousPage",transactions.hasPrevious());
			payload.put("data",data);
			return payload;
		}
		
		return null;
	}
	
	public Object getTransactionHistory(Integer pageNumber,Integer pageSize) {
		
		var transactions =  transactionRepository.findAll(PageRequest.of(pageNumber,pageSize));
//		transactions.nextPageable().
		//var transactionsPayload:TransactionHistory = 
//		Integer pageSize;
//		   Integer currentPage;
//		   Integer nextPage;
//		   Boolean hasNextPage;
//		   Boolean hasPreviousPage;
		Map<String,Object> transactionsMap = new HashMap<String,Object>();
		transactionsMap.put("transactions", transactions.get().collect(Collectors.toList()));
		transactionsMap.put("pageSize", transactions.getSize());
		transactionsMap.put("currentPage",transactions.getNumber());
		transactionsMap.put("nextPage",transactions.hasNext()?transactions.nextPageable().getPageNumber() : null);
		transactionsMap.put("hasNextPage",transactions.hasNext());
		transactionsMap.put("hasPreviousPage",transactions.hasPrevious());
		transactionsMap.put("totalItems", transactions.getTotalElements());
		return transactionsMap;
		
	}
	
public Object getTransactionHistoryByAccountNumber(String accountNumber,Integer pageNumber,Integer pageSize) {
		
		var transactions =  transactionRepository
			.findByAccountId(accountNumber,PageRequest.of(pageNumber ==null?0:pageNumber,pageSize ==null?100:pageSize));
//		transactions.nextPageable().
		//var transactionsPayload:TransactionHistory = 
//		Integer pageSize;
//		   Integer currentPage;
//		   Integer nextPage;
//		   Boolean hasNextPage;
//		   Boolean hasPreviousPage;
		Map<String, Object> transactionsMap = new HashMap<>();
		transactionsMap.put("transactions", transactions.get().collect(Collectors.toList()));
		transactionsMap.put("pageSize", transactions.getSize());
		transactionsMap.put("currentPage",transactions.getNumber());
		transactionsMap.put("nextPage",transactions.hasNext()?transactions.nextPageable().getPageNumber() : null);
		transactionsMap.put("hasNextPage",transactions.hasNext());
		transactionsMap.put("hasPreviousPage",transactions.hasPrevious());
		transactionsMap.put("totalItems", transactions.getTotalElements());
		return transactionsMap;
		
	}

}
