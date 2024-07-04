package net.sasakonnect.wallet.services.sme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.ResponseDto.sme.SmeAccRoleDto;
import net.sasakonnect.wallet.ResponseDto.sme.SmeRoleDto;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.RolePermission;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Enterprise;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRolePermission;
import net.sasakonnect.wallet.repository.sme.EnterpriseRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRoleRepository;
import net.sasakonnect.wallet.repository.sme.SmePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeRolePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeRoleRepository;
import net.sasakonnect.wallet.repository.sme.SmeUserRoleRepository;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

@Service
public class SmeRoleService {
	@Autowired
	SmeRoleRepository smeRoleRepository;
	@Autowired
	EnterpriseRepository  enterpriseRepository;
	@Autowired
	SmeUserRoleRepository smeUserRoleRepository;
	@Autowired
	SmePermissionRepository smePermissionRepository;
	@Autowired
	SmeRolePermissionRepository smeRolePermissionRepository;
	@Autowired
	SmeAccountRepository smeAccountRepository;
	@Autowired
	SmeAccountRoleRepository smeAccountRoleRepository;
	@Autowired
	SmeUserService smeUserService;
	public ResponseEntity<Object> createSmeRole(SmeRoleDto roleDto){
		Optional<Enterprise> enteprise = this.enterpriseRepository.findById(roleDto.getEnterpriseId());
		if(enteprise.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Uknown enterprise supplied");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		Optional<SmeRole> smeRoleExists = this.smeRoleRepository.findByRoleNameAndEnterprise(roleDto.getName(),enteprise.get());
		if(smeRoleExists.isPresent()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Role already exists");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		var smeRole = SmeRole.builder().roleName(roleDto.getName()).description(roleDto.getDescription()).enterprise(enteprise.get()).build();
		try {
			var savedRole = this.smeRoleRepository.save(smeRole);
			Map<String,Object> map = new HashMap<>();
			map.put("success",true);
			map.put("message","Role created successfully");
			map.put("role", savedRole);
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {
			ex.printStackTrace();
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}
	
	public ResponseEntity<Object> createSmeAccRole(SmeAccRoleDto roleDto){
		Optional<SmeAccount> smeacc = this.smeAccountRepository.findByAccountNo(roleDto.getAccountId());
		if(smeacc.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Uknown account");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
	  String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	  if(!smeacc.get().getSme().getId().equalsIgnoreCase(smeId)) {
		  throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
	  }
		Optional<SmeAccountRole> smeRoleExists = this.smeAccountRoleRepository.findByNameAndSmeAccount(roleDto.getName(),smeacc.get());
		if(smeRoleExists.isPresent()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Role already exists");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		var smeAccRole = SmeAccountRole.builder().name(roleDto.getName()).description(roleDto.getDescription()).smeAccount(smeacc.get()).build();
		try {
			var savedRole = this.smeAccountRoleRepository.save(smeAccRole);
			Map<String,Object> map = new HashMap<>();
			map.put("success",true);
			map.put("message","Role created successfully");
			map.put("role", savedRole);
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {
			ex.printStackTrace();
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}
	
  
  public ResponseEntity<Object> getRoles(Integer pageNumber,Integer pageSize){
	  Page<SmeRole> rolesPage = this.smeRoleRepository.findAll(PageRequest.of(pageNumber, pageSize));
	  Map<String,Object> resmap = new HashMap<>();
	  if(!rolesPage.isEmpty()) {
		  var roles = rolesPage.stream().map(role->{
			  Map<String,Object> map = new HashMap<>();
			  map.put("name",role.getRoleName());
			  map.put("description", role.getDescription());
			  map.put("id",role.getId());
			  return map;
		  }).collect(Collectors.toList());
		  resmap.put("success",true);
		  resmap.put("message", "Request complete");
		  resmap.put("roles",roles);
		  ResponsePagerClass<SmeRole> page =  ResponsePagerClass.<SmeRole>builder()
	    		    .page(rolesPage)
	    		    .build();
		 resmap.putAll(page.getPagingInfo());
		  return ResponseEntity.status(HttpStatus.OK).body(resmap);
	  }else {
		  resmap.put("success",true);
		  resmap.put("message", "Request complete");
		  resmap.put("roles",new ArrayList<>());
		  return ResponseEntity.status(HttpStatus.OK).body(resmap);
	  }
	  
	  
  } 
  
  public ResponseEntity<Object> getRolePermissions(String roleId){
	  return null;
  }
  
  public ResponseEntity<Object> getSmeUserRole(SmeCorporate user){
	  return null;
  }


  public void insertPermissionsNotAttachedToRole(SmeRole role,User user, List<String> permissionIds) {
		// Call the custom repository method to insert permissions not attached to the
		// role

		for (String permissionId : permissionIds) {
			SmePermissions permission = this.smePermissionRepository.findById(permissionId).orElse(null);

			if (permission != null) {
				var rolePermission = SmeRolePermission.builder().smePermission(permission).smeRole(role).creator(user).build();
				
//				this.rolePermissionRepository

				this.smeRolePermissionRepository.save(rolePermission);
			}
			// Handle the case where the permission with the provided ID does not exist.
		}

	}
  
  public Optional<SmeRole> findRoleById(String roleId){
	  return this.smeRoleRepository.findById(roleId);
  }
  
  public List<SmeRole> findRolesByName(String roleName){
	  return this.smeRoleRepository.findAllByRoleName(roleName);
  }
}
