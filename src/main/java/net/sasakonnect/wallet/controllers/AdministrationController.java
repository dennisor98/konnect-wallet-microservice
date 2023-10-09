package net.sasakonnect.wallet.controllers;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sasakonnect.wallet.annotations.RequirePermission;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;

@RequestMapping("/administration")
@Tag(name = "Administration", description = "Back Office  routes")

public class AdministrationController {
	@GetMapping("/upload/app")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateSuperApp.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateSuperApp.PERMISSION)
	public String upload() throws AccountNotFoundException {
		// Logic to retrieve targetDomainObject
		// For example: String targetDomainObject = someService.getTargetDomainObject();

		return "account: ";
	}

}
