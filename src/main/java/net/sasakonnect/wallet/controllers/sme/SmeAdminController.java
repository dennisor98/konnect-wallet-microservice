package net.sasakonnect.wallet.controllers.sme;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.sme.ConfirmSmeDto;
import net.sasakonnect.wallet.RequestDto.sme.CreateEnterpriseDto;
import net.sasakonnect.wallet.RequestDto.sme.CreateSmeDto;
import net.sasakonnect.wallet.RequestDto.sme.LLCInformationDto;
import net.sasakonnect.wallet.RequestDto.sme.LlcSmeMemberDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeAccountDocuments;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.RequirePermission;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.services.sme.SmeService;

@RequestMapping("/enterprise")
@Tag(name = "SME", description = "SME Back Office  routes")
@CustomController()
@Slf4j
@RestController()
public class SmeAdminController {
	@Autowired
	SmeService smeService;

	@PostMapping("/create")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanCreateAnEnterprise.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanCreateAnEnterprise.PERMISSION)
	public Object createEnterprise(@RequestBody CreateEnterpriseDto createEnterpriseDto) {
		var enterprise = this.smeService.createEnterprise(createEnterpriseDto);
		var enterpriseResponse = new HashMap<String, Object>();
		enterpriseResponse.put("name", enterprise.getName());
		enterpriseResponse.put("industry", enterprise.getIndustry());
		enterpriseResponse.put("ownership", enterprise.getOwnership());
		enterpriseResponse.put("mission", enterprise.getMission());
		enterpriseResponse.put("createdBy",
				enterprise.getCreator().getFirstName() + " " + enterprise.getCreator().getLastName());

		enterpriseResponse.put("vision", enterprise.getVision());
		return ResponseEntity.ok(enterpriseResponse);

	}

	@GetMapping("/list")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewAnEnterpriseListing.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanViewAnEnterpriseListing.PERMISSION)
	public Object listAllEnterprise(
			@Parameter(description = "Page number (starts from 0)", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
		return this.smeService.getEnterprise(page, size);

	}

	@GetMapping("/sme")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewAnEnterpriseListing.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanViewAnEnterpriseListing.PERMISSION)
	public Object getSmeAccount(
			@Parameter(description = "Page number (starts from 0)", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
		return this.smeService.getSmeAccount(page, size);

	}

	@PostMapping("/sme")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewAnEnterpriseListing.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanViewAnEnterpriseListing.PERMISSION)
	public Object createSmeAccount(@RequestBody CreateSmeDto createSmeSto) {
		return this.smeService.createSme(createSmeSto);

	}

	@PostMapping("/sme/confirmPhone")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '"
			+ GlobalPermissionConstants.CanConfirmOnboardingSmeAccountOtp.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanConfirmOnboardingSmeAccountOtp.PERMISSION)
	public Object confirmSmePhoneNumber(@RequestBody ConfirmSmeDto confirmsmeDto) {
		return this.smeService.confirmSmePhoneNumber(confirmsmeDto);

	}

	@PostMapping("/sme/information")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '"
			+ GlobalPermissionConstants.CanConfirmOnboardingSmeAccountOtp.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanConfirmOnboardingSmeAccountOtp.PERMISSION)
	public Object confirmSmePhoneNumber(@RequestBody LLCInformationDto confirmsmeDto) {
		return this.smeService.registerLccInformation(confirmsmeDto);

	}

	@PostMapping("/sme/member")
//	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRegisterMemberToLlc.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanRegisterMemberToLlc.PERMISSION)
	public Object addLccMember(@RequestBody LlcSmeMemberDto llcSmeMember) {
		return this.smeService.registerLLcMember(llcSmeMember);

	}

	@PostMapping("/sme/documents")
	@Operation(summary = "Upload SME Account Document", description = "Upload an SME account document")
	public Object uploadDocument(@Valid @RequestBody SmeAccountDocuments document) {
		return this.smeService.uploadSmeAccountDocuments(document);

	}

}
