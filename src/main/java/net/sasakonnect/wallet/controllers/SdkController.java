package net.sasakonnect.wallet.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.MerchantKeyDto;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.RequestDto.SdkRequestOpenId;
import net.sasakonnect.wallet.RequestDto.SdkSearchCustomer;
import net.sasakonnect.wallet.RequestDto.SdkSearchCustomers;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.RateLimit;
import net.sasakonnect.wallet.annotations.SdkMiddleware;
import net.sasakonnect.wallet.annotations.ServiceInteractionMiddleware;
import net.sasakonnect.wallet.annotations.TransactionMiddleware;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.WalletClientService;
import net.sasakonnect.wallet.tools.RequestSigner;
import net.sasakonnect.wallet.workers.MerchantWoker;

@RequestMapping("/sdk")
@Tag(name = "Software Kits", description = "Api's to talk with sdks")
@CustomController()

public class SdkController {
	@Autowired
	WalletClientService walletClientService;
	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	private final TransactionService transactionService;
	@Autowired
	MerchantWoker merchantWorker;
	@Autowired
	UserService userService;

	@Autowired
	private ClientAppsBean clientDataService;

	public SdkController(TransactionService transactionService, ClientAppsBean clientDataService) {
		this.transactionService = transactionService;
		this.clientDataService = clientDataService;
	}

	@PostMapping("pay")
	@TransactionMiddleware()
	@SdkMiddleware()
	public Object payUtility(
			@Parameter(example = "37c8043a43adca4368607e5742a10d501c0cb990a26906603818f18ad8d15882", name = "app-key", description = "Provide app key of the app you created on dashboard", in = ParameterIn.HEADER, required = true) @RequestHeader("app-key") String appKey,

			@RequestBody() @Valid SdkPayDto sdkpayDto) {
		var walletClientService = this.walletClientService.payThroughSdk(sdkpayDto);

		// System.out.print(walletClientService.);
		merchantWorker.notifyMerchantIncomingPayment(walletClientService, sdkpayDto);
		return walletClientService;
	}
	@PostMapping("customer")
	@ServiceInteractionMiddleware()
	@RateLimit(3)
	public Object checkCustomer(
			@Parameter(example = "d388a3ababb6c3a2851f1ad112d8037c1350b32fe93623edda82",
			name = "secret-key", 
			description = "Provide app key of the app you created on dashboard", 
			in = ParameterIn.HEADER, 
			required = true) 
			@RequestHeader("secret-key") String appSecret,

			@RequestBody() @Valid SdkSearchCustomer sdkSearchCustomer) {
		//ignored  country code just for brevity
		Map<Object, Object> message= new HashMap<>();
		
		var clientData = clientDataService.getWalletClient();
		if (clientData.getEnabled() && clientData.getDeletedAt() == null) {
			var phoneNumber=sdkSearchCustomer.getPhoneNumber();
			var user= this.userService.findUserByPhoneNumberLoadUserWallet(phoneNumber .substring(Math.max(0, phoneNumber.length() - 9)),sdkSearchCustomer.getCountryCode());
			if(user.isEmpty()) {
				
				message.put("message","user not found");
				
				
			    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}else {
				User founduser= user.get();
				
				message.put("openId",founduser.getOpenId());
				message.put("countryCode", founduser.getCountryCode());
				message.put("firstName",founduser.getFirstName());
				message.put("lastName",founduser.getLastName());
				message.put("middleName",founduser.getMiddleName());
				message.put("mobile",founduser.getMobile());
				message.put("verified",founduser.getUserWallets().isEmpty()?false:true);
				message.put("createdAt",founduser.getCreatedAt());
				

				
				
			    return ResponseEntity.status(HttpStatus.OK).body(message);
			}
		}
	
		message.put("message","could not validate key");
		
		
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
//		var walletClientService = this.walletClientService.payThroughSdk(sdkpayDto);
//
//		// System.out.print(walletClientService.);
//		merchantWorker.notifyMerchantIncomingPayment(walletClientService, sdkpayDto);
//		return walletClientService;
	}

	@PostMapping("customers")
	@ServiceInteractionMiddleware()
	@RateLimit(3)
	public Object checkCustomers(
			@Parameter(example = "d388a3ababb6c3a2851f1ad112d8037c1350b32fe93623edda82", 
			name = "secret-key", description = "Provide app key of the app you created on dashboard", in = ParameterIn.HEADER, required = true) @RequestHeader("secret-key") String appSecret,

			@RequestBody() @Valid() SdkSearchCustomers sdkSearchCustomer) {
		//ignored  country code just for brevity
		
	
		Map<Object, Object> wrapper= new HashMap<>();
		var clientData = clientDataService.getWalletClient();
		if (clientData.getEnabled() && clientData.getDeletedAt() == null) {
			
			var phoneNumbers=sdkSearchCustomer.createPhonePairs();
			
			var userList= this.userService.findUserPhoneNumberAndCountryCode(phoneNumbers);
			
			
			List<Map<Object, Object>> data= userList.stream().map((founduser)->{
					Map<Object, Object> message= new HashMap<>();
					message.put("openId",founduser.getOpenId());
					message.put("countryCode", founduser.getCountryCode());
					message.put("firstName",founduser.getFirstName());
					message.put("lastName",founduser.getLastName());
					message.put("middleName",founduser.getMiddleName());
					message.put("mobile",founduser.getMobile());
					message.put("verified",founduser.getUserWallets().isEmpty()?false:true);
					message.put("createdAt",founduser.getCreatedAt());
					return message;
				}).collect(Collectors.toList());;
				
				wrapper.put("payload", data);
				wrapper.put("sucess", true);
				

				
				
			    return ResponseEntity.status(HttpStatus.OK).body(wrapper);
			
		}
		wrapper.put("success", false);
		wrapper.put("message","could not validate key");
		
		
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(wrapper);

	}

	@PostMapping("merchant")

	public Object getMerchant(@RequestBody() @Valid MerchantKeyDto merchnantKeyDto) {
		return this.walletClientService.findMerchantByClientAppKey(merchnantKeyDto.getMerchant());
	}

	@GetMapping("transaction/{id}")
	@SdkMiddleware()
	public Object getOneTransaction(
			@Parameter(example = "37c8043a43adca4368607e5742a10d501c0cb990a26906603818f18ad8d15882", name = "app-key", description = "Provide app key of the app you created on dashboard", in = ParameterIn.HEADER, required = true) @RequestHeader("app-key") String appKey,
			@PathVariable String id) {
		if (id == null) {
			return ResponseEntity.notFound();
		}
		return this.transactionService.getTrasactionStatus(id);
	}

	@PostMapping("openId")
	@ServiceInteractionMiddleware
	@Parameter(example = "37c8043a43adca4368607e5742a10d501c0cb990a26906603818f18ad8d15882", name = "secret-key", description = "Provide app secret of the app you created on dashboard", in = ParameterIn.HEADER, required = true)
	public Object createOpenIdForUser(@Valid @RequestBody() SdkRequestOpenId sdkOpenRequest) {
		var clientData = clientDataService.getWalletClient();
		if (clientData.getEnabled() && clientData.getDeletedAt() == null) {
			return this.walletClientService.createOpenidSession(sdkOpenRequest, clientData);
		}
		return null;

	}

}
