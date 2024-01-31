package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.domain.Transaction;
public class AnalyticsService {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	TransactionRepository  transactionRepository;
	
  public Object getSummary() {
	  Long totalAccounts = this.userRepository.count();
	  Long totalTransactions = this.transactionRepository.count();
	  Map<String,Object> resMap  = new HashMap<>();
	  Map<String,Object> map = new HashMap<>();
	  map.put("totalTransactions",totalTransactions);
	  map.put("totalAccounts", totalAccounts);
	  resMap.put("payload",map);
	  return resMap;
  }
  
  public Object getRecentTransactions() {
	  List<Transaction> latestTransactions = this.transactionRepository.findRecentTransactions();
	  Map<String,Object> resMap = new HashMap<>();
	  if(latestTransactions !=null) {
		var ltransactions =   latestTransactions.stream().map(transaction ->{
			  Map<String,Object> map =  new HashMap<>();
			  map.put("created_at",transaction.getCreatedAt());
			  map.put("id",transaction.getId());
			  map.put("receiver_id",transaction.getOppoAccountId());
			  map.put("sender_id",transaction.getAccountId());
			  map.put("sender_name",transaction.getAccountName());
			  map.put("receiver_name",transaction.getOppoAccountName());
			  map.put("transaction_status", transaction.getTxStatus());
			  return map;
		  }).collect(Collectors.toList());
		resMap.put("success",true);
		resMap.put("transactions",ltransactions);
		return ResponseEntity.status(HttpStatus.OK).body(resMap);
	  }else {
		  resMap.put("success", true);
		  resMap.put("transactions",new ArrayList<>()); 
		  return ResponseEntity.status(HttpStatus.OK).body(resMap);
	  }
  }
}
