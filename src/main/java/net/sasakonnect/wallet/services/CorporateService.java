package net.sasakonnect.wallet.services;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.RequestDto.Corporate;
import net.sasakonnect.wallet.RequestDto.Corporate.CorporateBuilder;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.CorporateDetails.CorporateDetailsBuilder;
import net.sasakonnect.wallet.repository.CorporateDetailsRepository;

@Service
public class CorporateService {
	@Autowired
	CorporateDetailsRepository corporateRepository;
  public Object createCorporateDetails(CorporateDetails corporate) {
	  return this.corporateRepository.save(corporate);
  }
  
  public Object activateCorporateAccount(Map<String,Object> request) {
	  Map<String,Object> resMap = new HashMap<>(); 
	  if(request.get("id")!=null) {
		var corporateAccount=  this.corporateRepository.findById(request.get("id").toString());
		if (corporateAccount.isPresent()) {
			var account = corporateAccount.get();
			account.setIsActive(true);
			try {
				this.corporateRepository.save(account);
				resMap.put("success", "true");
				resMap.put("message", "Account activated");
				return ResponseEntity.status(HttpStatus.OK).body(resMap);
			} catch (Exception ex) {
				resMap.put("success", "false");
				resMap.put("message", "Failed");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
			}
			
		}else {
			resMap.put("success", "false");
			resMap.put("message","User not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
		}
	  }else {
		    resMap.put("success", "false");
			resMap.put("message","Required fields are empty");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
	  }
	
	  
	 
	  
  }
//  
//  public Object verifyEmail(String email,Boolean isVerified) {
//	  return this.corporateRepository.verifyCorporateEmail(email,isVerified);
//  }
  
//  
//  
  public Object getCorporateAccounts() {
	  return this.corporateRepository.findAll();
  }
  
  
  

}
