package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.domain.FinancialContact;
import net.sasakonnect.wallet.repository.FinancialContactRepository;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

@Service
public class FinancialContactService {
	
	@Autowired
	FinancialContactRepository financialContactRepository;
      public void saveTransactionContact(FinancialContact finacialContact) {
    	  this.financialContactRepository.save(finacialContact);
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
}
