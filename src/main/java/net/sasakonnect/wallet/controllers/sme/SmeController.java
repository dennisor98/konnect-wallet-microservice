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
import net.sasakonnect.wallet.RequestDto.sme.SmeAssignRoleDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeUserLogin;
import net.sasakonnect.wallet.ResponseDto.sme.SmeAccRoleDto;
import net.sasakonnect.wallet.ResponseDto.sme.SmeCorporateDto;
import net.sasakonnect.wallet.ResponseDto.sme.SmeRoleDto;
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
	@PostMapping("user")
	@HasSmePermission(GlobalSmePermissionConstants.CanAddSmeUser.PERMISSION)
	public ResponseEntity<Object> createSmeUser(@Valid @RequestBody() SmeCorporateDto smeUserDto){
		return this.smeUserService.createSmeUser(smeUserDto.getUserId());
	}
	
	@SmeCorporate
	@HasSmePermission(GlobalSmePermissionConstants.CanCreateSmeRole.PERMISSION)
	@PostMapping("role")
	public ResponseEntity<Object> createSmeRole(@Valid @RequestBody() SmeRoleDto roleDto){
		return this.createSmeRole(roleDto);
	}
	
	@SmeCorporate
	@HasSmePermission(GlobalSmePermissionConstants.CanCreateSmeAccountRole.PERMISSION)
	@PostMapping("account/role")
	public ResponseEntity<Object> createSmeAccountRole(@Valid @RequestBody() SmeAccRoleDto roleDto){
		return this.smeRoleService.createSmeAccRole(roleDto);
	}
	
	@SmeCorporate
	@HasSmePermission(GlobalSmePermissionConstants.CanAssignSmeRole.PERMISSION)
	@PostMapping("user/assignRole")
	public ResponseEntity<Object> assignRole(@Valid @RequestBody() SmeAssignRoleDto roleDto){
		return this.smeRoleService.assignSmeUserRole(roleDto);
	}
	
	@SmeCorporate
	@HasSmePermission(GlobalSmePermissionConstants.CanAssignAccountRole.PERMISSION)
	@PostMapping("user/account/assignRole")
	public ResponseEntity<Object> assignAccountRole(@Valid @RequestBody() SmeAssignRoleDto roleDto){
		return this.smeRoleService.assignSmeAccountRole(roleDto);
	}
	
	@SmeCorporate
	@HasSmePermission(GlobalSmePermissionConstants.CanAssignRolePermissions.PERMISSION)
	@PostMapping("role/assign/permissions")
	public ResponseEntity<Object> assignSmeRolePermissions(@Valid @RequestBody() SmeAssignRoleDto roleDto){
		return this.assignSmeRolePermissions(roleDto);
	}
	
	@SmeCorporate
	@HasSmePermission(GlobalSmePermissionConstants.CanAssignRolePermissions.PERMISSION)
	@PostMapping("account/role/assign/permissions")
	public ResponseEntity<Object> assignSmeAccountRolePermissions(@Valid @RequestBody()SmeAssignRoleDto roleDto){
		return this.assignSmeAccountRolePermissions(roleDto);
	}
	
	
	@SmeCorporate
	@GetMapping("roles")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetRoles.PERMISSION)
	public  ResponseEntity<Object> getRoles(@RequestParam(name="pageNumber",required=true,defaultValue="0") Integer pageNumber, @RequestParam(name="pageSize",required=true,defaultValue="10") Integer pageSize){
		return this.smeRoleService.getRoles(pageNumber, pageSize);
	}
	
	@SmeCorporate
	@GetMapping("account/roles")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetRoles.PERMISSION)
	public  ResponseEntity<Object> getAccountRoles(@RequestParam(name="pageNumber",required=true,defaultValue="0") Integer pageNumber, @RequestParam(name="pageSize",required=true,defaultValue="10") Integer pageSize){
		return this.smeRoleService.getRoles(pageNumber, pageSize);
	}
	
	
	@SmeCorporate
	@GetMapping("permissions")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetPermissions.PERMISSION)
	public  ResponseEntity<Object> getPermissionByCategory(@RequestParam(name="categoryName",required=false) String category,@RequestParam(name="pageNumber",required=true,defaultValue="0") Integer pageNumber, @RequestParam(name="pageSize",required=true,defaultValue="10") Integer pageSize ){
		return  category != null ? this.smePermissionService.getPermissionsByCategoryName(category,pageNumber, pageSize) : this.smePermissionService.getPermissions(pageNumber, pageSize);
	}
	
	@SmeCorporate
	@GetMapping("members")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetPermissions.PERMISSION)
	public  ResponseEntity<Object> getSmeMembers(@RequestParam(name="pageNumber",required=true,defaultValue="0") Integer pageNumber, @RequestParam(name="pageSize",required=true,defaultValue="10") Integer pageSize ){
		return  this.smeUserService.getSmeMembers(pageNumber, pageSize);
				
	}
	
	
	
	
}
