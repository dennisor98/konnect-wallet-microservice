package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.account.ConfirmAccDTO;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.enums.IdentityType;
import net.sasakonnect.wallet.enums.PinResetQuestionaire;
import net.sasakonnect.wallet.repository.UserRepository;

@Service
public class PinResetService {
	@Autowired
	UserRepository userRepository;

	
  public ResponseEntity<Object> confirmAccountExists(@Valid ConfirmAccDTO identity){
	  if(identity.getIdentityType().toString().equalsIgnoreCase(IdentityType.MOBILE_NUMBER.getValue())) {
		  if(!this.mobileHasAccount(identity.getAnswer())) {
			  Map<String,Object> map = new HashMap<>();
			  map.put("proceed",false);
			  map.put("message","Account with phone number not found");
			  
			  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		  }else {
			  Map<String,Object> map = new HashMap<>();
			  map.put("proceed",true);
			  map.put("message","Account available");
			  
			  return ResponseEntity.status(HttpStatus.OK).body(map);
		  }
	  }
	  
	  if(identity.getIdentityType().toString().equalsIgnoreCase(IdentityType.ID_NUMBER.getValue())) {
		  if(!this.idNumberHasAccount(identity.getAnswer())) {
			  Map<String,Object> map = new HashMap<>();
			  map.put("proceed",false);
			  map.put("message","Account with ID Number not found");
			  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		  }else {
			  Map<String,Object> map = new HashMap<>();
			  map.put("proceed",true);
			  map.put("message","Account available");
			  return ResponseEntity.status(HttpStatus.OK).body(map);
		  }
	  }
	  
	  return null;
  }
  private ResponseEntity<Object> getNextQuestionaire(int index){
	  String[]  questions = new String []{
                        PinResetQuestionaire.MOBILE_NUMBER.getValue(),
                        PinResetQuestionaire.FULL_NAME.getValue(),
                        PinResetQuestionaire.ID_NUMBER.getValue(),
                        PinResetQuestionaire.DOB.getValue(),
                        PinResetQuestionaire.LAST_IN_TRANSACTION.getValue(),
                        PinResetQuestionaire.LAST_OUT_TRANSACTION.getValue(),
  };
	  return null;
  }
  
  private Object getQuestionaireAnswer(PinResetQuestionaire question,User user) {
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
}
