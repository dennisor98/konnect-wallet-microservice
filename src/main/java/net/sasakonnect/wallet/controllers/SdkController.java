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
			@Parameter(name = "custom-header", description = "Custom header description", in = ParameterIn.HEADER, required = true) @RequestHeader("custom-header") String customHeader,
			@RequestBody() @Valid SdkPayDto sdkpayDto) {
		return this.walletClientService.payThroughSdk(sdkpayDto);
	}

}
