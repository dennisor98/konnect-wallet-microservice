package net.sasakonnect.wallet.services;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.RequestDto.Corporate;
import net.sasakonnect.wallet.RequestDto.Corporate.CorporateBuilder;
import net.sasakonnect.wallet.RequestDto.VerifyCorporate;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.CorporateDetails.CorporateDetailsBuilder;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.CorporateDetailsRepository;
import net.sasakonnect.wallet.repository.UserRepository;

@Service
public class CorporateService {
	@Autowired
	CorporateDetailsRepository corporateRepository;
	@Autowired
	UserRepository userRepository;
	
  public Object createCorporateDetails(CorporateDetails corporate) {
	  return this.corporateRepository.save(corporate);
  }
  
//  public Object activateCorporateAccount(Map<String,Object> request) {
//	  Map<String,Object> resMap = new HashMap<>(); 
//	  if(request.get("id")!=null) {
//		var corporateAccount=  this.corporateRepository.findById(request.get("id").toString());
//		if (corporateAccount.isPresent()) {
//			var account = corporateAccount.get();
//			account.setIsActive(true);
//			try {
//				this.corporateRepository.save(account);
//				resMap.put("success", "true");
//				resMap.put("message", "Account activated");
//				return ResponseEntity.status(HttpStatus.OK).body(resMap);
//			} catch (Exception ex) {
//				resMap.put("success", "false");
//				resMap.put("message", "Failed");
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
//			}
//			
//		}else {
//			resMap.put("success", "false");
//			resMap.put("message","User not found");
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
//		}
//	  }else {
//		    resMap.put("success", "false");
//			resMap.put("message","Required fields are empty");
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
//	  }
//	
//	  
//	 
//	  
//  }
  
  public Object activateCorporateAccount(VerifyCorporate corporate){
	 Optional <CorporateDetails> corpAccount = this.corporateRepository.findById(corporate.getCorporateId());
	 Map<String,Object> map =  new HashMap<>();
	 Map<String,Object> responseObject = new HashMap<>();
	 if(corpAccount.isEmpty()) {
		 map.put("message","Account not found");
		 map.put("success","false");
		 responseObject.put("payload", map);
		 return ResponseEntity.status(HttpStatus.OK).body(responseObject);
	 }else {
		 Optional<User> user = this.userRepository.getUserByCorporateId(corpAccount.get());
		 if(user.isEmpty()) {
			 map.put("message","Not active user found for the corporate account");
			 map.put("success","false");
			 responseObject.put("payload", map);
			 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseObject);
		 }else {
			 var cop = corpAccount.get();
			 if(corporate.getIsActive() == cop.getIsActive() || corporate.getIsVerified() == cop.getIsVerified()) {
				 map.put("message","Account already in this status");
				 map.put("success","false");
				 responseObject.put("payload", map);
				 return ResponseEntity.status(HttpStatus.OK).body(responseObject);
			 }else {
				 try {
					 cop.setIsActive(corporate.getIsActive());
					 cop.setIsVerified(corporate.getIsVerified());
					 this.corporateRepository.save(cop);
					 map.put("message","Account status modified");
					 map.put("success","true");
					 responseObject.put("payload", map);
					 return ResponseEntity.status(HttpStatus.OK).body(responseObject);
				 }catch(Exception ex) {
					 map.put("message", "A system error occured while processing request");
					 map.put("success","false");
					 responseObject.put("payload", map);
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
				 }
			 }
		 }
	 }
	
  }
//  
//  public Object verifyEmail(String email,Boolean isVerified) {
//	  return this.corporateRepository.verifyCorporateEmail(email,isVerified);
//  }
  
//  
//  
  public Object getCorporateAccounts() {
	  Map<String,Object> map = new HashMap<>();
	  Map<String,Object> responseObject = new HashMap<>();
	  try {
		  var users =  this.corporateRepository.findAll();
		  map.put("message", "Request successfull");
		  map.put("success","true");
		  map.put("users",users);
		  responseObject.put("payload",map);
		return ResponseEntity.status(HttpStatus.OK).body(responseObject);
	  }catch(Exception ex) {
		  map.put("success","false");
		  map.put("message","Internal Server Error");
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
	  }
	  
  }
  
  
  

}
