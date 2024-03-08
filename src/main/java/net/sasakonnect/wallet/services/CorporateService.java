package net.sasakonnect.wallet.services;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sasakonnect.wallet.RequestDto.Corporate;
import net.sasakonnect.wallet.RequestDto.Corporate.CorporateBuilder;
import net.sasakonnect.wallet.RequestDto.UserRoleDTO;
import net.sasakonnect.wallet.RequestDto.VerifyCorporate;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.CorporateDetails.CorporateDetailsBuilder;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.CorporateDetailsRepository;
import net.sasakonnect.wallet.repository.RoleRepository;
import net.sasakonnect.wallet.repository.UserRepository;

@Service
public class CorporateService {
	@Autowired
	CorporateDetailsRepository corporateRepository;
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	RoleService roleService;

	@Transactional
 public Object createCorporateDetails(Corporate corporate) {
	 Map<String, Object> map = new HashMap<>();
	 Map<String, Object> payloadMap = new HashMap<>();
//	  Optional<Role> role = this.roleRepository.findByRoleName("CORPORATE");
	  Optional<User> user = this.userRepository.findByMobile(corporate.getPhone());
//	  if(role.isPresent() && user.isPresent()) {
//		  UserRoleDTO userRole = UserRoleDTO.builder()
//				  .roleId(role.get().getId())
//				  .userId(user.get().getId())
//				  .build();
//		  try {
//			   this.roleService.attachUserToRole(userRole);
//		  }catch(Exception ex) {
//			 System.out.println("ERROR"+ex);	
//		  }
		  if(user.isPresent()) {
		  CorporateDetails cop  =  CorporateDetails.builder()
				  .phone(corporate.getPhone())
				  .corporateEmail(corporate.getEmail())
				  .isActive(corporate.getIsActive())
				  .larkOpenId(corporate.getLark_open_id())
				  .build();
		  try {
			  this.corporateRepository.save(cop);
			  user.get().setCorporate(cop);
              this.userRepository.save(user.get());
			  
		  }catch(Exception ex) {
			  System.out.println("ERROR"+ex);
		  }
		  
		 
		 map.put("success",true);
		 map.put("message","User added to corporate");
		 payloadMap.put("payload",map);
		 return ResponseEntity.status(HttpStatus.OK).body(payloadMap);
	  }else {
		     map.put("success",false);
			 map.put("message","User not found");
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  
  }
  

  
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
			 if(corporate.getIsActive() == cop.getIsActive()) {
				 map.put("message","Account already in this status");
				 map.put("success","false");
				 responseObject.put("payload", map);
				 return ResponseEntity.status(HttpStatus.OK).body(responseObject);
			 }else {
				 try {
					 cop.setIsActive(corporate.getIsActive());
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
