package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.domain.FinancialContact;
import net.sasakonnect.wallet.enums.FinancialContactType;
import net.sasakonnect.wallet.repository.FinancialContactRepository;
import net.sasakonnect.wallet.tools.ResponsePagerClass;
@Slf4j
@Service
public class FinancialContactService {
	
	@Autowired
	FinancialContactRepository financialContactRepository;
      public void saveTransactionContact(FinancialContact finacialContact) {
    	  Optional<FinancialContact> contact = this.financialContactRepository.findByAccountIdAndOppoAccountIdAndTxType(finacialContact.getAccountId(), finacialContact.getOppoAccountId(),finacialContact.getTxType());
    	  if(contact.isPresent()) {
    		var c =   contact.get();
    		c.setUpdatedAt(new Date());
    		c.setOppoAccountId(finacialContact.getOppoAccountName());
    		this.financialContactRepository.save(c);
    	  }else {
    		  this.financialContactRepository.save(finacialContact);
    	  }
    	  
      }
      
      
      public ResponseEntity<Object> getFinancialContacts(String accountId,Integer pageNumber,Integer pageSize){
    	  Page<FinancialContact> contacts = this.financialContactRepository.findByAccountId(accountId,PageRequest.of(pageNumber, pageSize));
    	  Map<String,Object> map = new HashMap<>();
    	  Map<String,Object> resMap = new HashMap<>();
    	  if(!contacts.isEmpty()) {
    		  
    		var fContacts =  contacts.stream().map(c->{
    			  Map<String,Object> cMap = new HashMap<>();
    			  cMap.put("accountId",c.getOppoAccountId());
    			  cMap.put("subAccountId",c.getOppoSubAccountId());
    			  cMap.put("accountName",c.getOppoAccountName());
    			  return map;
    		  }).collect(Collectors.toList());
    		map.put("success",true);
  		    map.put("message","Request successfull");
  		  ResponsePagerClass<FinancialContact> page =  ResponsePagerClass.<FinancialContact>builder()
  		    		    .page(contacts)
  		    		    .build();
    		map.put("contacts",fContacts);
    		map.put("pager", page);
    	  }else {
    		  map.put("success",true);
    		  map.put("message","Request successfull");
    		  map.put("contacts",new ArrayList<>());  
    	  }
    	  resMap.put("payload",map);
    	  return ResponseEntity.status(HttpStatus.OK).body(resMap) ;
    	  
      }
      
      public ResponseEntity<Object> searchOppoAccounIdInfo(String oppoAccountId){
    	  Optional<FinancialContact> contact  = this.financialContactRepository.findByoppoAccountId(oppoAccountId);
    	  if(contact.isPresent()) {
    		  var c = contact.get();
    		  Map<String,Object> map =  new HashMap<>();
    		  map.put("success", true);
    		  map.put("message","Request successful");
    		  Map<String,Object> cmap =  new HashMap<>();
    		  cmap.put("oppoAccountId",c.getOppoAccountId());
    		  cmap.put("oppoAccountName",c.getOppoAccountName());
    		  cmap.put("lastUpdatedAt", contact);
    		  map.put("accountInfo",cmap);
    		  
    		  return ResponseEntity.status(HttpStatus.OK).body(map);
    	  }
    	  return null;
      }
      
      public ResponseEntity<Object> getTransactionContacts(String accountId,String txType,Integer pageNumber,Integer pageSize){
    	  Page<FinancialContact> recentTransactions = null;
    	  
    	  if(txType.equalsIgnoreCase(FinancialContactType.MPESA.getValue())) {
    		  recentTransactions = this.financialContactRepository.findMpesaTransactionContacts(accountId, PageRequest.of(pageNumber,pageSize));
    		 
    		  
    	  }
    	  
    	  if(txType.equalsIgnoreCase(FinancialContactType.PAYBILL.getValue())) {
    		  recentTransactions = this.financialContactRepository.findPaybillContacts(accountId, PageRequest.of(pageNumber,pageSize));
    	  }
    	  
    	  if(txType.equalsIgnoreCase(FinancialContactType.TILL.getValue())) {
    		  recentTransactions = this.financialContactRepository.findTillContacts(accountId, PageRequest.of(pageNumber,pageSize));
    	  }
    	  
    	  if(txType.equalsIgnoreCase(FinancialContactType.WALLET.getValue())) {
    		  recentTransactions = this.financialContactRepository.findWalletContacts(accountId, PageRequest.of(pageNumber,pageSize));
    	  }
    	  
    	  if(txType.equalsIgnoreCase(FinancialContactType.PESA_LINK.getValue())) {
    		  recentTransactions = this.financialContactRepository.findPesaLinkContacts(accountId, PageRequest.of(pageNumber,pageSize));
    	  }
    			  
    	  Map<String,Object> payload = new HashMap<>();
    	  if(recentTransactions !=null) {
    		  var rt =  recentTransactions.stream().map(t->{
    				 Map<String,Object> map = new HashMap<>();
    				 map.put("accountId",t.getOppoAccountId());
    				 map.put("accountName",t.getOppoAccountName() );
    				 map.put("subAccountId",t.getOppoSubAccountId());		
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
      
     
}
