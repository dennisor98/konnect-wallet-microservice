package net.sasakonnect.wallet.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.PinResetDto;
import net.sasakonnect.wallet.RequestDto.account.ConfirmAccDTO;
import net.sasakonnect.wallet.domain.PinresetIssues;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserWallet;
import net.sasakonnect.wallet.enums.IdentityType;
import net.sasakonnect.wallet.enums.PinResetQuestionaire;
import net.sasakonnect.wallet.repository.PinResetIssuesRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.UserWalletRepository;

@Service
public class PinResetService {
	@Autowired
	UserRepository userRepository;
	
	
	@Autowired
	UserWalletRepository userWalletRepository;
	
	@Autowired
	WalletService walletService;
	
	@Autowired
	TransactionService  transactionService;
	
	@Autowired
	LarkService larkService;
	
	@Autowired
	PinResetIssuesRepository pinResetIssuesRepository;

	
  public ResponseEntity<Object> confirmAccountExists(String idNumber){	 
		  if(!this.idNumberHasAccount(idNumber)) {
			  Map<String,Object> map = new HashMap<>();
			  map.put("proceed",false);
			  map.put("message","Account with ID Number not found");
			  return ResponseEntity.status(HttpStatus.OK).body(map);
		  }else {
			  Map<String,Object> map = new HashMap<>();
			  map.put("proceed",true);
			  map.put("message","Account available");
			  return ResponseEntity.status(HttpStatus.OK).body(map);
		  }
	  
	  
  }
  
  @Transactional
  public ResponseEntity<Object> requestPinReset(PinResetDto req){
	  try {
	  float totalScore  = 0;
	  Optional<User> user  = this.userRepository.findByIdNumber(req.getIdNumber());
	  if(user.isPresent()) {
		  if((req.getFirstName()).equalsIgnoreCase(user.get().getFirstName()) && req.getLastName().equalsIgnoreCase(user.get().getLastName())) {
			  totalScore+=1;  
		  }
		  if(req.getMobileNumber().equalsIgnoreCase(user.get().getMobile())) {
			  totalScore+=1;
		  }
		  
//		  if(req.getDateofBirth().toString().equals(user.get().getBirthday())){
//			  totalScore+=1; 
//		  }
		  
		  if(req.getLastReceivedAmount() == this.getUserLastReceivedAmount(user.get())){
			  totalScore+=1;
		  }
		  
		  if(req.getLastSentAmount() == this.getUserLastTransactedAmount(user.get())) {
			  totalScore+=1;
		  }
		  
		  if(req.getBalance() == this.getWalletBalanceByUser(user.get())) {
			  totalScore +=1;
		  }
		  
		  float percentageScore = (totalScore/5)*100;
		  User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		  Optional<UserWallet> wallet = this.userWalletRepository.findByUserId(user.get().getId());
		  var pinReset =   PinresetIssues.builder()
			    .accountOwner(user.get())
			    .accountId(wallet.isPresent()?wallet.get().getWallet().getAccountId():null)
			    .approverLarkOpenId(req.getApprover())
			    .resetReason(req.getResetReason().getValue())
			    .validationScore(percentageScore)
			    .requesterId(loggedInUser)
				.build();
		      
		  this.pinResetIssuesRepository.save(pinReset);
		  this.larkService.sendPinResetApprovalNotification(req,wallet.isPresent()?wallet.get().getWallet():null,loggedInUser,percentageScore);
		  Map<String,Object> map = new HashMap<>();
		  map.put("success", true);
		  map.put("message", "Request submission success");
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  }catch(Exception ex) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success", false);
		  map.put("message", "Error processing request");
		  ex.printStackTrace();
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  
	  return null;
  }
  


  

  private Boolean mobileHasAccount(String mobile) {
	  Optional<User> user  = this.userRepository.findByMobile(mobile);
	  
	  return user.isPresent();
  }
  
  private Boolean idNumberHasAccount(String idNumber) {
	  Optional<User> user  = this.userRepository.findByIdNumber(idNumber);
	  return user.isPresent();
  }
  
  private Double getWalletBalanceByUser(User user) {
		 var balance = this.walletService.getWalletBalance(user);
	  return balance;
  }
  
  private Double getUserLastReceivedAmount(User user) {
	  Optional<UserWallet> wallet = this.userWalletRepository.findByUserId(user.getId());
	  if(wallet.isPresent()) {
		  Optional<Transaction> lastReceived = this.transactionService.getLastInTransaction(wallet.get().getWallet().getAccountId()); 
		  if(lastReceived.isPresent()) {
			  BigDecimal bigDecimalValue = new BigDecimal(lastReceived.get().getAmount().toString());
			  double doubleValue = Double.parseDouble(bigDecimalValue.toString());
			  return doubleValue;
		  }
	  }
	 return 0.00;  
  }
  
  private Double getUserLastTransactedAmount(User user) {
	  Optional<UserWallet> wallet = this.userWalletRepository.findByUserId(user.getId());
	  if(wallet.isPresent()) {
		  Optional<Transaction> lastReceived = this.transactionService.getLastOutTransaction(wallet.get().getWallet().getAccountId()); 
		  if(lastReceived.isPresent()) {
			  BigDecimal bigDecimalValue = new BigDecimal(lastReceived.get().getAmount().toString());
			  double doubleValue = Double.parseDouble(bigDecimalValue.toString());
			  return doubleValue;
		  }
	  }
	 return 0.00;  
  }
  
}
