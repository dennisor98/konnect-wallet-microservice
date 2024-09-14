package net.sasakonnect.wallet.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.CorporateLoginDTO;
import net.sasakonnect.wallet.RequestDto.CreatePasswordDto;
import net.sasakonnect.wallet.RequestDto.LoginOtpResendDto;
import net.sasakonnect.wallet.RequestDto.OpenIdRequest;
import net.sasakonnect.wallet.RequestDto.PasswordResetDto;
import net.sasakonnect.wallet.RequestDto.UserDeviceToken;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.annotations.AdminwindowMiddleware;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.IsCorporate;
import net.sasakonnect.wallet.annotations.RefreshMiddleware;
import net.sasakonnect.wallet.provider.FirebaseMessage;
import net.sasakonnect.wallet.provider.FirebaseWrapper;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.redis.JobProducer;
import net.sasakonnect.wallet.tools.redis.Queueable;

@RequestMapping("user")
@CustomController()
@Tag(name = "User", description = "User routes")

public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private JobProducer<Queueable<List<FirebaseMessage>>> jobProducer;

	@Autowired
	FirebaseWrapper firebaseWrapper;

	@PostMapping("userLogin")
	public ResponseEntity<ObjectNode> getAll(@Valid @RequestBody UserLogin loginDto) {
		return userService.userLogin(loginDto);
	}
	
	@PostMapping("login/resendOtp")
	public ResponseEntity<Object> rendLoginOtp(@Valid @RequestBody LoginOtpResendDto otpDto) {
		return userService.resendLoginOtp(otpDto);
	}
	

	@PostMapping("corporateLogin")
	public Object corporateSignin(@Valid @RequestBody CorporateLoginDTO loginDTO) {
		return userService.corporateLogin(loginDTO);
	}

	@PostMapping("confirmOtp")
	public ResponseEntity confirmOtp(@Valid @RequestBody ConfirmOtp confirmOtp) {
		return userService.verifyOtp(confirmOtp);
	}
	
	@PostMapping("admin/confirmOtp")
	public ResponseEntity confirmAdminOtp(@Valid @RequestBody ConfirmOtp confirmOtp) {
		return userService.verifyAdminOtp(confirmOtp);
	}
	
	@PostMapping("admin/password/set")
	@AdminwindowMiddleware()
	public Object createAdminPassword(@Valid @RequestBody CreatePasswordDto passwordDto) {
		return userService.createAdminPassword(passwordDto);
	}
	
	@PostMapping("admin/password/update")
//	@IsCorporate()
	public Object updateAdminPassword(@Valid @RequestBody PasswordResetDto passwordDto) {
		return userService.updateAdminPassword(passwordDto);
	}
	
	@PostMapping("admin/verifyPin")
	@AdminwindowMiddleware()
	public Object verifyAdminPassword(@Valid @RequestBody CreatePasswordDto passwordDto) {
		return userService.verifyAdminPin(passwordDto);
	}


	@PostMapping("refresh/token")
	@RefreshMiddleware()
	public ResponseEntity refreshToken() {
		return userService.createRefreshToken();
	}

	@PostMapping("/messaging/token")
	public ResponseEntity<Object> token(@Valid @RequestBody UserDeviceToken userDeviceToken) {
		return userService.updateFirebaseToken(userDeviceToken);
	}

	@PostMapping("openId")
	public ResponseEntity getMyOpenId(@Valid @RequestBody OpenIdRequest openId) {

		return userService.createUserOpenId(openId);
	}

	@GetMapping("financialContact")
	public ResponseEntity financialContact() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("payload", transactionService.getFancanctialContact());
		map.put("success", "true");

		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	@GetMapping("profile")
	public ResponseEntity<Object> getUserProfile() {
		return this.userService.getAuthenticatedUserProfile();
	}
	
	@GetMapping("permissions")
	public ResponseEntity<Object> getUserRolesAndPermissions() {
		return this.userService.getUserPermissions();
	}
	
	@PostMapping("profile/image/upload")
	public ResponseEntity<Object> uploadProfileImage(
		@Valid	@RequestParam("file") MultipartFile file
			){
		return this.userService.uploadProfileImage(file);
	}
	
	@GetMapping("account/confirm")
	public ResponseEntity<Object> confirmAccountExists(@RequestParam(name="idNumber",required=true) String idNumber,@RequestParam(name="mobileNumber",required=true) String mobile){
		return this.userService.confirmAccountByIdNumber(idNumber,mobile);
	}
	
	@GetMapping("account/confirm/action")
	public ResponseEntity<Object> confirmAccountAction(@RequestParam(name="actionType",required=true) String actionType){
		
		return null;
	}
	
	
	
	
	

}
