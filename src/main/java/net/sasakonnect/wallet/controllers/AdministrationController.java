package net.sasakonnect.wallet.controllers;

import javax.security.auth.login.AccountNotFoundException;

import net.sasakonnect.wallet.services.CorporateService;
import net.sasakonnect.wallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.Corporate;
import net.sasakonnect.wallet.RequestDto.VerifyCorporate;
import net.sasakonnect.wallet.RequestDto.Corporate.CorporateBuilder;
import net.sasakonnect.wallet.RequestDto.VerifyEmailDTO;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.RequestDto.admin.CheckUserAccount;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.RequirePermission;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.CorporateDetails.CorporateDetailsBuilder;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.WalletClientService;
import net.sasakonnect.wallet.services.WalletService;
import java.util.HashMap;
import java.util.Map;
@RequestMapping("/administration")
@Tag(name = "Administration", description = "Back Office  routes")
@CustomController()
@CrossOrigin(origins = "http://localhost:4200")
public class AdministrationController {
	@Autowired
	WalletClientService walletClientService;
	@Autowired
	WalletService walletService;

	@Autowired
	UserService userService;
	
	@Autowired
	CorporateService corporateService;

	@GetMapping("/upload/app")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateSuperApp.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateSuperApp.PERMISSION)
	public String upload() throws AccountNotFoundException {
		// Logic to retrieve targetDomainObject
		// For example: String targetDomainObject = someService.getTargetDomainObject();
		return "account: ";
	}

	@PostMapping("/create/app")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object createApp(@Valid @RequestBody() WalletClientDTO walletClientDto) {
		return this.walletClientService.createWallectClientApp(walletClientDto);
	}

	@PostMapping("/attach/paymentAccount")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object attachPaymentAccount(@Valid @RequestBody() WalletClientAccountDto walletClientAccount) {
		return this.walletClientService.createWalletClientAccount(walletClientAccount);
	}

	@PostMapping("/check/account/status")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object checkUserAccountStatus(@Valid @RequestBody() CheckUserAccount checkUserAccount) {
		return this.walletService.checkUserAccountStatus(checkUserAccount);
	}

	@GetMapping("/users/getAll")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object getAllUsers(){
		return  this.userService.getAllUsers();
	}
	
	@GetMapping("/users/corporate/getAll")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object getAllCorporateUser(){
		return  this.userService.getCorporateUsers();
	}
	
	
	
	
	@PostMapping("/user/corporate/create")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object createCorporateDetails(@Valid @RequestBody Corporate param) {	
		CorporateDetails corporate = CorporateDetails.builder()
				.corporateEmail(param.getCorporateEmail())
				.phone(param.getPhone())
				.isVerified(param.getIsVerified())
				.isEmailVerified(param.getIsEmailVerified())
				.isActive(param.getIsActive())
				.build();
		User user =  userService.getUserById(param.getUserId()).get();
		if(user !=null) {
			user.setCorporate(corporate);
			return this.corporateService.createCorporateDetails(corporate);
		}else {
			Map<String,Object> map =  new HashMap<>();
			map.put("message","User Not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
		}
		
	    
	}
	
	
	@PostMapping("/user/corporate/account/activate")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object activateCorporateAccount(@Valid @RequestBody VerifyCorporate  request) {
		return this.corporateService.activateCorporateAccount(request);
	}
	
//	@PostMapping("/user/corporate/email/verify")
//	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
//	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
//    public Object verifyCorporateEmail(@RequestBody  VerifyEmailDTO request) {
//		return this.corporateService.verifyEmail(request.getEmail(),true);
//	}
	
	@GetMapping("/user/corporate/get")
	public Object getCorporateEmails() {
		return this.corporateService.getCorporateAccounts();
	}

}
