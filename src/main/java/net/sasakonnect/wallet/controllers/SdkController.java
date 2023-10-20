package net.sasakonnect.wallet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.RequestDto.MerchantKeyDto;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.annotations.SdkMiddleware;
import net.sasakonnect.wallet.annotations.TransactionMiddleware;
import net.sasakonnect.wallet.services.WalletClientService;

@RequestMapping("/sdk")
@Tag(name = "Software Kits", description = "Api's to talk with sdks")
@CustomController()

public class SdkController {
	@Autowired
	WalletClientService walletClientService;

	@PostMapping("pay")
	@TransactionMiddleware()
	@SdkMiddleware()
	public Object payUtility(
			@Parameter(example = "37c8043a43adca4368607e5742a10d501c0cb990a26906603818f18ad8d15882", name = "app-key", description = "Provide app key of the app you created on dashboard", in = ParameterIn.HEADER, required = true) @RequestHeader("app-key") String appKey,

			@RequestBody() @Valid SdkPayDto sdkpayDto) {
		return this.walletClientService.payThroughSdk(sdkpayDto);
	}

	@PostMapping("merchant")
	@TransactionMiddleware()

	public Object getMerchant(
			@Parameter(example = "37c8043a43adca4368607e5742a10d501c0cb990a26906603818f18ad8d15882", name = "app-key", description = "Provide app key of the app you created on dashboard", in = ParameterIn.HEADER, required = true) @RequestHeader("app-key") String appKey,

			@RequestBody() @Valid MerchantKeyDto merchnantKeyDto) {
		return this.walletClientService.findMerchantByClientAppKey(merchnantKeyDto.getMerchant());
	}

}
