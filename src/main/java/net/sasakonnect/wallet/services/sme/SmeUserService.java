package net.sasakonnect.wallet.services.sme;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.sme.SmeUserLogin;
import net.sasakonnect.wallet.ResponseDto.sme.SmeUserResponseDto;
import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeMember;
import net.sasakonnect.wallet.domain.sme.SmePassword;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.sme.SmeMemberRepository;
import net.sasakonnect.wallet.repository.sme.SmePasswordRepository;
import net.sasakonnect.wallet.repository.sme.SmeRepository;
import net.sasakonnect.wallet.services.OtpService;
import net.sasakonnect.wallet.services.OtpSmsService;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.RequestSigner;
@Slf4j
@Service
public class SmeUserService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	SmeRepository smeRepository;
	
	@Autowired
	SmeMemberRepository smeMemberRepository;
	@Autowired
	SmePasswordRepository  smePasswordRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	JwtService  jwtService;
	@Autowired
	OtpService otpService;
	@Autowired
	OtpSmsService otpSmsService;

	
  public ResponseEntity<ObjectNode> smeLogin(SmeUserLogin loginDto){
	  
	  var mobileNumber = loginDto.getPhoneNumber().trim();
	  var validPhone = mobileNumber.substring(mobileNumber.length() -9);
	  
	  JsonNodeFactory factory = JsonNodeFactory.instance;
	  ObjectNode map = factory.objectNode();

	  Optional<User> user =  this.userRepository.findByMobile(validPhone);
	  
	  
	  if(user.isEmpty()) {
		  map.put("success", false);
		  map.put("message","User not found");
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
	  }
	  log.info(user.toString());
	  
	  Optional<SmeMember> smeMember = this.smeMemberRepository.findSmeMemberByUser(user.get());
	 if(smeMember.isEmpty()) {
		 map.put("success", false);
		 map.put("message","Account not found");
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
	 }
	 
	 var smeUser = smeMember.get();
//	 map.put("sme", smeUser);
	 Optional<SmePassword> smePassword = this.smePasswordRepository.findSmePasswordBySmeMemberId(smeUser);
	 if(smePassword.isEmpty()) {
		 map.put("success", false);
		 map.put("message","Password not set");
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
	 }else {
		 if(this.validatePassword(loginDto.getPassword(), smePassword.get().getPassword())) {
			  return this.otpSmsService.sendSmeUserSms(loginDto, null, user);	
//			 return ResponseEntity.status(HttpStatus.OK).body(map);
		 }
		 map.put("success", false);
		 map.put("message", "Incorrect cridentials");
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		 
	 }
	 
  }
  
  private boolean validatePassword(String rawPassword,String encryptedPassword) {
		 return passwordEncoder.matches(rawPassword, encryptedPassword);
  }
  
  
  public ResponseEntity<Object> createDefaultPassword(String memberId){
	  Optional<SmeMember> smeMemberOptional =  this.smeMemberRepository.findById(memberId);
	  if(smeMemberOptional.isEmpty()) {
		  Map<String,Object> map =  new HashMap<>();
		  map.put("success",false);
		  map.put("message","Invalid sme member");
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  
	  var smeMember = smeMemberOptional.get();
	  var password =  this.generateRandomString();
	  var defaultPassword =  SmePassword.builder()
	  .isDefault(true)
	  .member_id(smeMember)
	  .password(this.passwordEncoder.encode(password))
	  .build();
	  
	  try {
		  this.smePasswordRepository.save(defaultPassword);
		  Map<String,Object> map  = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Default password created successful");
		  map.put("default_password",password);
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }catch(Exception ex) {
		  ex.printStackTrace();
		  Map<String,Object> map  = new HashMap<>();
		  map.put("success",false);
		  map.put("message","Something went wrong");
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
	  }
	  
	  
  }
  

  private  String generateRandomString() {
      String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8}$";
      Pattern pattern = Pattern.compile(regex);

      String randomString;
      do {
          randomString = generateRandomStringInternal();
      } while (!pattern.matcher(randomString).matches());

      return randomString;
  }
  private  String generateRandomStringInternal() {
	  SecureRandom random = new SecureRandom();
      StringBuilder sb = new StringBuilder(8);
      String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@$!%*?&";
      for (int i = 0; i < 8; i++) {
          int randomIndex = random.nextInt(chars.length());
          char randomChar = chars.charAt(randomIndex);
          sb.append(randomChar);
      }
      return sb.toString();
  }
 
 
  
  public ResponseEntity<Object> verifySmeUserOtp(ConfirmOtp otpDto) {
	  Optional<Otp> otp = this.otpSmsService.verifyOtp(otpDto);
	  if(otp.isPresent()) {
		  if(!otp.get().isValid()) {
		Map<String,Object> map = new HashMap<>();
		  map.put("success", false);
		  map.put("message","OTP code invalid");
		  
		  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		  }
		  
		  var user = otp.get().getUser();
		  
		  if(user !=null) {
			var response =  SmeUserResponseDto.builder()
				 .token(jwtService.generateSmeMemberToken(user)).refreshToken(jwtService.generateRefreshToken(user))
					.middleName(user.getMiddleName())
					.open_id(user.getOpenId())
					.updatedAt(user.getUpdatedAt())
		            .profileImage(user.getProfileImage())
					.createdAt(user.getCreatedAt()).id(user.getId())
					.firstName(user.getFirstName()).lastName(user.getLastName())
					.mobile(user.getMobile()).countryCode(user.getCountryCode()).build();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				try {
					return ResponseEntity.ok(objectMapper.writeValueAsString(response));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		  
	  }
	  return null;
	
  }
  
  
  
  public Optional<Sme> findSmeByPhone(String phone){
	  return this.smeRepository.findSmeByMobile(phone);
  }
}
