package net.sasakonnect.wallet.controllers;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.annotations.RequirePermission;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.services.WalletClientService;

@RequestMapping("/administration")
@Tag(name = "Administration", description = "Back Office  routes")
@CustomController()
public class AdministrationController {
	@Autowired
	WalletClientService walletClientService;

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
}
