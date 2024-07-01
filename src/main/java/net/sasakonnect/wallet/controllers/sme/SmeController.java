package net.sasakonnect.wallet.controllers.sme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.sme.SmeUserLogin;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.sme.HasSmePermission;
import net.sasakonnect.wallet.annotations.sme.SmeCorporate;
import net.sasakonnect.wallet.constant.sme.GlobalSmePermissionConstants;
import net.sasakonnect.wallet.services.sme.SmePermissionService;
import net.sasakonnect.wallet.services.sme.SmeRoleService;
import net.sasakonnect.wallet.services.sme.SmeUserService;

@RequestMapping("/sme")
@Tag(name = "SME", description = "SME User  routes")
@CustomController()
@Slf4j
@RestController()
public class SmeController {
	@Autowired
	SmeUserService smeUserService;
	@Autowired
	SmeRoleService smeRoleService;
	@Autowired
	SmePermissionService smePermissionService;
	
	@PostMapping("/login")
	public ResponseEntity<ObjectNode> smeLogin(@Valid @RequestBody SmeUserLogin loginDto ){
		return this.smeUserService.smeLogin(loginDto);
	}
	
	
	
	@PostMapping("/verifyOtp")
	public ResponseEntity<Object> verifySmeOtp(@Valid @RequestBody ConfirmOtp SmeotpDto ){
		return this.smeUserService.verifySmeUserOtp(SmeotpDto);
	}
	
	@SmeCorporate
	@GetMapping("roles")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetRoles.PERMISSION)
	public  ResponseEntity<Object> getRoles(@RequestParam(name="pageNumber",required=true,defaultValue="0") Integer pageNumber, @RequestParam(name="pageSize",required=true,defaultValue="10") Integer pageSize){
		return this.smeRoleService.getRoles(pageNumber, pageSize);
	}
	
	
	@SmeCorporate
	@GetMapping("permissions")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetPermissions.PERMISSION)
	public  ResponseEntity<Object> getPermissionByCategory(@RequestParam(name="categoryName",required=false) String category,@RequestParam(name="pageNumber",required=true,defaultValue="0") Integer pageNumber, @RequestParam(name="pageSize",required=true,defaultValue="10") Integer pageSize ){
		return  category != null ? this.smePermissionService.getPermissionsByCategoryName(category,pageNumber, pageSize) : this.smePermissionService.getPermissions(pageNumber, pageSize);
	}
}
