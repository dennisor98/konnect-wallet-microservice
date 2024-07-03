package net.sasakonnect.wallet.services.sme;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.sme.SmeUserLogin;
import net.sasakonnect.wallet.ResponseDto.sme.SmeUserResponseDto;
import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;
import net.sasakonnect.wallet.domain.sme.SmePassword;
import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRolePermission;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.sme.SmeCorporateRepository;
import net.sasakonnect.wallet.repository.sme.SmeMemberRepository;
import net.sasakonnect.wallet.repository.sme.SmePasswordRepository;
import net.sasakonnect.wallet.repository.sme.SmeRepository;
import net.sasakonnect.wallet.repository.sme.SmeRolePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeUserRoleRepository;
import net.sasakonnect.wallet.services.OtpService;
import net.sasakonnect.wallet.services.OtpSmsService;
import net.sasakonnect.wallet.services.UserService;
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
	public JwtService  jwtService;
	@Autowired
	OtpService otpService;
	@Autowired
	OtpSmsService otpSmsService;
	@Autowired
	SmeCorporateRepository smeCorporateRepository;
	@Autowired
	SmeUserRoleRepository smeUserRoleRepository;
	@Autowired
	SmeRolePermissionRepository smeRolePermissionRepository;
	
	
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
	  
	  List<SmeCorporate> smeMember = this.smeCorporateRepository.findSmeCorporateByUser(user.get());
	 if(smeMember.isEmpty()) {
		 map.put("success", false);
		 map.put("message","Account not found");
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
	 }
	 
	 SmeCorporate smeUser = smeMember.get(0);
//	 log.info("{sme}"+smeUser);
//	 map.put("sme", smeUser);
	 Optional<SmePassword> smePassword = this.smePasswordRepository.findSmePasswordBySmeCorporaterId(smeUser);
	 if(smePassword.isEmpty()) {
		 map.put("success", false);
		 map.put("message","Password not set");
		 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
	 }else {
		 if(this.validatePassword(loginDto.getPassword(), smePassword.get().getPassword())) {
			 if(smeMember.size() > 1) {
				 List<Sme> smes = this.smeCorporateRepository.findSmesByUser(user.get()); 
				 Map<String,Object> smeresmap = new HashMap<>();
				 smeresmap.put("multiacount",true);
				 smeresmap.put("options",smes.stream().map(c->{
					 Map<String,Object> optionsmap = new HashMap<>();
					 map.put("id",c.getId());
					 map.put("name",c.getAccountDetails().getBusinessName());
					 return optionsmap;
				 }));
				 
			 }
			  return this.otpSmsService.sendSmeUserSms(loginDto,smeMember.get(0).getSmes(),null, user);	
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
  
  
  public ResponseEntity<Object> createDefaultPassword(String userId){
	  Optional<User> user = this.userRepository.findById(userId);
       if(user.isEmpty()) {
    	   Map<String,Object> map =  new HashMap<>();
 		  map.put("success",false);
 		  map.put("message","Invalid userId");
 		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
       }
	  List<SmeCorporate> smecorporatelist =  this.smeCorporateRepository.findSmeCorporateByUser(user.get());
	  if(smecorporatelist.isEmpty()) {
		  Map<String,Object> map =  new HashMap<>();
		  map.put("success",false);
		  map.put("message","Invalid Request");
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  var password =  this.generateRandomString();
	  smecorporatelist.stream().map(sc->{
		  var defaultPassword =  SmePassword.builder()
				  .isDefault(true)
				  .corporate_id(sc)
				  .password(this.passwordEncoder.encode(password))
				  .build();

		  try {
			 return this.smePasswordRepository.save(defaultPassword);
		  }catch(Exception ex) {
			  ex.printStackTrace();
			  Map<String,Object> cmap  = new HashMap<>();
			  cmap.put("success",false);
			  cmap.put("message","Something went wrong");
			  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cmap);
		  }
	  }).collect(Collectors.toList());
	 
	 
	  Map<String,Object> map  = new HashMap<>();
	  map.put("success",true);
	  map.put("message","Default password created successful");
	  map.put("default_password",password);
	  return ResponseEntity.status(HttpStatus.OK).body(map);
  }
  
  public ResponseEntity<Object> createSmeUser(String userId){
	  Optional<User> user = this.userRepository.findById(userId);
	  if(user.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success", false);
		  map.put("message","User not found");
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
	  
	  String smeId =  this.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	  Optional<Sme> sme = this.smeRepository.findById(smeId);
	  if(sme.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Invalid smeId");
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  var u = user.get();
	  Optional<SmeCorporate> smeCorporporate = this.smeCorporateRepository.findSmeCorporateByUserAndSmes(u,sme.get());
	  if(smeCorporporate.isPresent()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success", false);
		  map.put("message","User already have account");
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	var smeCorp =  SmeCorporate.builder()
	  .user(u)
	  .smes(sme.get())
	  .build();
	
   this.smeCorporateRepository.save(smeCorp);
	  
	  Map<String,Object> map = new HashMap<>();
	  map.put("success", false);
	  map.put("message","Sme corporate account created successfully");
	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
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
		  if(!otp.get().isValid() || otp.get().getSme() == null) {
		Map<String,Object> map = new HashMap<>();
		  map.put("success", false);
		  map.put("message","OTP code invalid");
		  
		  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		  }
		  
		  var user = otp.get().getUser();
		  
		  if(user !=null) {
			var response =  SmeUserResponseDto.builder()
				 .token(jwtService.generateSmeMemberToken(user,otp.get().getSme())).refreshToken(jwtService.generateRefreshToken(user))
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
			
			Map<String,Object> map = new HashMap<>();
			map.put("payload", response);
				try {
					return ResponseEntity.ok(map);
				} catch (Exception e) {
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
  
  public Optional<Sme> findSmeById(String id){
	  return this.smeRepository.findSmeById(id);
  }
  
  public Optional<SmeUserRole> getSmeUserRoleByUser(User user,Sme sme){
	  Optional<SmeCorporate> smeCorpOptional = this.smeCorporateRepository.findSmeCorporateByUserAndSmes(user,sme);
	  if(smeCorpOptional.isPresent()) {
		  return this.smeUserRoleRepository.findByUser(smeCorpOptional.get());
	  }

	  return Optional.empty();
  }
  
  public boolean findRolePermissionsByRole(SmeRole role, String permission) {
		Optional<SmePermissions> permissions = this.smeRolePermissionRepository.findBySmeRoleAndSmePermissions(role,permission);
		if (permissions.isEmpty()) {
			return false;
		}
		return true;
	}
  
  public ResponseEntity<Object> getSmeMembers(Integer pageNumber,Integer pageSize){
	  Page<SmeCorporate> smeUserspage = this.smeCorporateRepository.findAll(PageRequest.of(pageNumber, pageSize));
	  if(smeUserspage.isEmpty()) {
		  Map<String,Object> map  = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request complete");
		  map.put("users",new ArrayList<>());
		  
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  Map<String,Object> resmap = new HashMap<>();
	  var smeusers = smeUserspage.stream().map(u->{
		  Map<String,Object> map = new HashMap<>();
		  map.put("id",u.getId());
		  map.put("firstname", u.getUser().getFirstName());
		  map.put("lastname", u.getUser().getLastName());
		  map.put("mobile", u.getUser().getMobile());
		  return map;
	  }).collect(Collectors.toList());
	  resmap.put("success",true);
	  resmap.put("message","Request complete");
	  resmap.put("users",smeusers);
	  return ResponseEntity.status(HttpStatus.OK).body(resmap);
  }


}
