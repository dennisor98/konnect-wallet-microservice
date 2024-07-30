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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.sme.SmeAssignRoleDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeAssingRolePermissionDto;
import net.sasakonnect.wallet.ResponseDto.sme.SmeAccRoleDto;
import net.sasakonnect.wallet.ResponseDto.sme.SmeRoleDto;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Enterprise;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountPermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRolePermission;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountUserRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRolePermission;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;
import net.sasakonnect.wallet.repository.sme.EnterpriseRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountPermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRolePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRoleRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountUserRoleRepository;
import net.sasakonnect.wallet.repository.sme.SmePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeRepository;
import net.sasakonnect.wallet.repository.sme.SmeRolePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeRoleRepository;
import net.sasakonnect.wallet.repository.sme.SmeUserRoleRepository;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

@Slf4j
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
	SmeAccountUserRoleRepository  smeAccountUserRoleRepository;
	@Autowired
	SmeUserService smeUserService;
	@Autowired
	SmeAccountService smeAccountService;
	@Autowired
	SmeRepository smeRepository;
	@Autowired
	SmeAccountPermissionRepository smeAccountPermissionRepository;
	@Autowired
	SmeAccountRolePermissionRepository smeAccountRolePermissionRepository;
	@Autowired
	UserService userService;
	
	public ResponseEntity<Object> createSmeRole(SmeRoleDto roleDto){
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		   //verify that smeId in the authentication header is available
		   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				   .getRequest();
		   String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
		   Optional<Sme> sme =  this.smeRepository.findById(smeId);
		 if(sme.isPresent()){
			 var enterprise = sme.get().getEnterprise();
				Optional<SmeRole> smeRoleExists = this.smeRoleRepository.findByRoleNameAndEnterprise(roleDto.getName(),enterprise);
				if(smeRoleExists.isPresent()) {
					Map<String,Object> map = new HashMap<>();
					map.put("success",false);
					map.put("message","Role already exists");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
				}

				var smeRole = SmeRole.builder().roleName(roleDto.getName()).description(roleDto.getDescription()).enterprise(enterprise).build();
				try {
					var savedRole = this.smeRoleRepository.save(smeRole);
					Map<String,Object> map = new HashMap<>();
					map.put("success",true);
					map.put("message","Role created successfully");
					return ResponseEntity.status(HttpStatus.OK).body(map);
				}catch(Exception ex) {
					ex.printStackTrace();
					Map<String,Object> map = new HashMap<>();
					map.put("success",false);
					map.put("message","Something went wrong");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
				}
				
				
		 }
		
		 return null;

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
			Map<String,Object> rmap =  new HashMap<>();
			rmap.put("id",savedRole.getId());
			rmap.put("name",savedRole.getName());
			rmap.put("description",savedRole.getDescription());
			map.put("role",rmap);
			
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {
			ex.printStackTrace();
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}
	
	public ResponseEntity<Object> assignSmeUserRole(SmeAssignRoleDto roleDto) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user.getId().equalsIgnoreCase(roleDto.getUser_id())) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Operation forbidden");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		Optional<User> OptionalUser =  this.userService.findUserById(roleDto.getUser_id());
		if(OptionalUser.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Invalid user_id");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
		Optional<Sme> sme =  this.smeRepository.findById(smeId);
		if(sme.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Data cannot be processed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		} 
		Optional<SmeCorporate> smeCorp =  this.smeUserService.smeCorporateRepository.findSmeCorporateByUserAndSmes(OptionalUser.get(),sme.get());
		Optional<SmeRole> smeRole = this.smeRoleRepository.findById(roleDto.getRole_id());

		if(smeCorp.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Account not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		
		if(smeRole.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Invalid role_id");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
	
		
		Optional<SmeUserRole> userRoleExists = this.smeUserRoleRepository.findBySmeRoleAndUser(smeRole.get(),smeCorp.get());
		if(userRoleExists.isPresent()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Role already assigned");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		}
		
		var userHasRole = this.smeUserRoleRepository.findByUser(smeCorp.get());
		var userRole =  SmeUserRole.builder().smeRole(smeRole.get()).smeAccount(sme.get()).user(smeCorp.get()).build();
		try {
			if(userHasRole.isPresent()) {
				this.smeUserRoleRepository.delete(userHasRole.get());
			}
			this.smeUserRoleRepository.save(userRole);
			Map<String,Object> map =  new HashMap<>();
			map.put("success",true);
			map.put("message","Request completed.Role asssigned");
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","A server error encoutered");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}
	
	
	public ResponseEntity<Object> removeUserFromRole(SmeAssignRoleDto roleDto){
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user.getId().equalsIgnoreCase(roleDto.getUser_id())) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Operation forbidden");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		Optional<User> OptionalUser =  this.userService.findUserById(roleDto.getUser_id());
		if(OptionalUser.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Invalid user_id");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
		Optional<Sme> sme =  this.smeRepository.findById(smeId);
		if(sme.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Data cannot be processed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		} 
		Optional<SmeCorporate> smeCorp =  this.smeUserService.smeCorporateRepository.findSmeCorporateByUserAndSmes(OptionalUser.get(),sme.get());
		Optional<SmeRole> smeRole = this.smeRoleRepository.findById(roleDto.getRole_id());

		if(smeCorp.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Account not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		if(smeRole.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Invalid role_id");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		Optional<SmeUserRole> userRoleExists = this.smeUserRoleRepository.findBySmeRoleAndUser(smeRole.get(),smeCorp.get());
		if(userRoleExists.isEmpty()){
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Role mapping unavailable");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		try {
			this.smeUserRoleRepository.delete(userRoleExists.get());
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","Role mapping unavailable");
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {	
			ex.printStackTrace();
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","A server error occured");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}
	}
	
	 @Transactional
	    public ResponseEntity<Object> assignPermissionsToSmeRole(String[] permissionIds, String roleId) {
	        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        Optional<SmeRole> smerole = this.smeRoleRepository.findById(roleId);
	        
	        if (smerole.isPresent() && permissionIds.length > 0) {
	            List<SmeRolePermission> rolePermissions = new ArrayList<>();
	            
	            for (String id : permissionIds) {
	                Optional<SmePermissions> permission = this.smePermissionRepository.findById(id);
	                
	                if (permission.isPresent()) {
	                    Optional<SmeRolePermission> rolePermission = this.smeRolePermissionRepository.findBySmeRoleAndSmePermission(smerole.get(), permission.get());
	                    
	                    if (rolePermission.isEmpty()) {
	                        SmeRolePermission newRolePermission = SmeRolePermission.builder()
	                                .smePermission(permission.get())
	                                .smeRole(smerole.get())
	                                .creator(user)
	                                .build();
	                        rolePermissions.add(newRolePermission);
	                    }
	                }
	            }
	            
	            if (!rolePermissions.isEmpty()) {
	                try {
	                    this.smeRolePermissionRepository.saveAll(rolePermissions);
	                    Map<String,Object> map  = new HashMap<>();
	                    map.put("success",true);
	                    map.put("message","Request completed.Permissions updated");
	                    return ResponseEntity.status(HttpStatus.OK).body(map);
	                } catch (Exception ex) {
	                    log.error(ex.getMessage());
	                    Map<String,Object> map  = new HashMap<>();
	                    map.put("success",false);
	                    map.put("message","A server error occured");
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
	                }
	            }
	        }
	        
	        return null;
	    }
	 
	 public ResponseEntity<Object> assignPermissionsToSmeAccountRole(SmeAssingRolePermissionDto roleDto){
		  User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        Optional<SmeAccountRole> smerole = this.smeAccountRoleRepository.findById(roleDto.getRole_id());
	        
	        if (smerole.isPresent() && roleDto.getPermissionIds().length > 0) {
	        	List<SmeAccountRolePermission> rolePermissions = new ArrayList<>();

	        	for (String id : roleDto.getPermissionIds()) {
	        		Optional<SmeAccountPermissions> permission = this.smeAccountPermissionRepository.findById(id);

	        		if (permission.isPresent()) {
	        			Optional<SmeAccountRolePermission> rolePermission = this.smeAccountRolePermissionRepository.findRolePermissionByRoleAndPermission(smerole.get(), permission.get().getName());

	        			if (rolePermission.isEmpty()) {
	        				SmeAccountRolePermission newRolePermission = SmeAccountRolePermission.builder()
	        						.permission(permission.get())
	        						.role(smerole.get())
	        						.creator(user)
	        						.build();
	        				rolePermissions.add(newRolePermission);
	        			}
	        		}
	        	}
	            
	            if (!rolePermissions.isEmpty()) {
	                try {
	                    this.smeAccountRolePermissionRepository.saveAll(rolePermissions);
	                    Map<String,Object> map  = new HashMap<>();
	                    map.put("success",true);
	                    map.put("message","Permissions created ");
	                    return ResponseEntity.status(HttpStatus.OK).body(map);
	                } catch (Exception ex) {
	                    log.error(ex.getMessage());
	                    Map<String,Object> map  = new HashMap<>();
	                    map.put("success",false);
	                    map.put("message","A server error occured");
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
	                }
	            }
	        }
	        
	        return null;
	 }
	
	public ResponseEntity<Object> assignSmeAccountRole(SmeAssignRoleDto roleDto) {
		Optional<SmeAccountRole> smeRole = this.smeAccountRoleRepository.findById(roleDto.getRole_id());
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
		Optional<Sme> sme =  this.smeRepository.findById(smeId);
		if(sme.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message", "Uknown sme");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		
		Optional<SmeCorporate> smeCorp =  this.smeUserService.smeCorporateRepository.findSmeCorporateByUserAndSmes(user,sme.get());
		if(smeCorp.isEmpty()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message", "Unauthorized!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		if(smeRole.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid information");
		}
		
		
		if(!smeCorp.get().getSmes().getId().equalsIgnoreCase(smeId)) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message", "Unauthorized!Wrong sme access");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		Optional<SmeAccountUserRole> smeUserrole = this.smeAccountUserRoleRepository.findBySmeCorporateAndRole(smeCorp.get(),smeRole.get());
		if(smeUserrole.isPresent()){
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message", "Role already assigned to user");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		
		var userRole =  SmeAccountUserRole.builder().role(smeRole.get()).smeCorporate(smeCorp.get()).user(user).build();
		try {
			this.smeAccountUserRoleRepository.save(userRole);
			Map<String,Object> map =  new HashMap<>();
			map.put("success",true);
			map.put("message","Request completed.Role asssigned");
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {
//			log.error(ex.getMessage());
			Map<String,Object> map =  new HashMap<>();
			map.put("success",false);
			map.put("message","A server error encoutered");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	} 
	
  
  public ResponseEntity<Object> getRoles(Integer pageNumber,Integer pageSize){
	  Page<SmeRole> rolesPage = this.smeRoleRepository.findAll(PageRequest.of(pageNumber, pageSize));
	  Map<String,Object> resmap = new HashMap<>();
	  if(!rolesPage.isEmpty()) {
		  var roles = rolesPage.stream().map(role->{
			  Map<String,Object> map = new HashMap<>();
			  map.put("createdAt",role.getCreatedAt());
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
  
  
  public ResponseEntity<Object> geSmeAccountRolesBySme(Integer pageNumber,Integer pageSize) {
	  HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
	  String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	  Optional<Sme> sme =  this.smeRepository.findById(smeId);
	  Page<SmeAccountRole> rolespage =  this.smeAccountRoleRepository.findBySme(sme.get(),PageRequest.of(pageNumber,pageSize));
	  if(rolespage.isEmpty()) {
		  Map<String,Object> map =  new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request completed");
		  map.put("roles",new ArrayList<>());
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  
	  var roles  = rolespage.stream().map(r->{
		 Map<String,Object> map  = new HashMap<>();
		 map.put("id",r.getId());
		 map.put("name",r.getName());
		 map.put("description",r.getDescription());
		 return map;
	  }).collect(Collectors.toList());
	  Map<String,Object> map =  new HashMap<>();
	  map.put("success",true);
	  map.put("message","Request completed");
	  map.put("roles",roles);
	  return ResponseEntity.status(HttpStatus.OK).body(map);
  }
  
  public ResponseEntity<Object> getRolePermissions(String roleId){
	  Optional<SmeRole> smeRoleOptional =  this.smeRoleRepository.findById(roleId);
	  
	  if(smeRoleOptional.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",false);
		  map.put("message","Unknown role");
		  
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  var smerole = smeRoleOptional.get();
	  List<SmePermissions> smePermission =  this.smeRolePermissionRepository.findAllBySmeRole(smerole);
	  if(smePermission.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",false);
		  map.put("message","Request completed");
		  map.put("permissions",new ArrayList<>());
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  Map<String,Object> map = new HashMap<>();
	  map.put("success",false);
	  map.put("message","Request completed");
	  var smePermissions = smePermission.stream()
			  .map(p->{
				  Map<String,Object> pmap = new HashMap<>();
				  pmap.put("id",p.getId());
				  pmap.put("name",p.getName());
				  pmap.put("description",p.getDescription());
				  return pmap;
			  }).collect(Collectors.toList());
	  map.put("permissions",smePermissions);
	  map.put("totalItems",smePermission.size());
	  
	  return ResponseEntity.status(HttpStatus.OK).body(map);
  }
  
  public ResponseEntity<Object> getSmeUserRole(SmeCorporate user){
	  return null;
  }


  public void insertPermissionsNotAttachedToRole(SmeRole role,User user, List<String> permissionIds) {
		// Call the custom repository method to insert permissions not attached to the
		// role

		for (String permissionId : permissionIds) {
			Optional<SmePermissions> permission = this.smePermissionRepository.findById(permissionId);

			if (permission.isPresent()) {
				Optional<SmeRolePermission> smeRolePermission  = this.smeRolePermissionRepository.findBySmeRoleAndSmePermission(role, permission.get());
				if(smeRolePermission.isEmpty()) {
					var rolePermission = SmeRolePermission.builder().smePermission(permission.get()).smeRole(role).creator(user).build();
					this.smeRolePermissionRepository.save(rolePermission);

				}
				
				
//				this.rolePermissionRepository

			}
			// Handle the case where the permission with the provided ID does not exist.
		}

	}
  
  public Optional<SmeRole> findRoleById(String roleId){
	  return this.smeRoleRepository.findById(roleId);
  }
  
  public Optional<SmeAccountRole> findSmeAccRoleById(String roleId){
	  return this.smeAccountRoleRepository.findById(roleId);
  }
  
  public List<SmeRole> findRolesByName(String roleName){
	  return this.smeRoleRepository.findAllByRoleName(roleName);
  }
  
  public ResponseEntity<Object> getRoleUsers(String roleId){
	  Optional<SmeRole> smeRole = this.findRoleById(roleId);
	  if(smeRole.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",false);
		  map.put("message","Role not found");
		  
		  return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	  }
	  var role = smeRole.get();
	  List<SmeUserRole> smeUserRoles = this.smeUserRoleRepository.findBySmeRole(role);
	  if(smeUserRoles.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request completed");
		  map.put("users",new ArrayList<>());
		  return  ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  
	  var users = smeUserRoles.stream()
			  .map(rp->{
				  Map<String,Object> umap = new HashMap<>();
				  umap.put("id",rp.getUser().getUser().getId());
				  umap.put("user_role_id",rp.getId());
				  umap.put("date_added",rp.getCreatedAt());
				  umap.put("firstname",rp.getUser().getUser().getFirstName());
				  umap.put("middlename",rp.getUser().getUser().getMiddleName());
				  umap.put("lastname",rp.getUser().getUser().getLastName());
				  umap.put("mobile",rp.getUser().getUser().getMobile());
				 return umap; 
			  }).collect(Collectors.toList());
	  Map<String,Object> map = new HashMap<>();
	  map.put("success",true);
	  map.put("message","Request completed");
	  map.put("users",users);
	return ResponseEntity.status(HttpStatus.OK).body(map);
  }
}
