package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.PermissionsToRoleDTO;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.RolePermission;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.PermissionRepository;
import net.sasakonnect.wallet.repository.RolePermissionRepository;
import net.sasakonnect.wallet.repository.RoleRepository;

@Service
public class RoleService {
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	RolePermissionRepository rolePermissionRepository;
	@Autowired
	PermissionRepository permissionRepository;
	
	@Transactional
	public Role insertRole(Role role) {
		// Check if a role with the same name already exists
		Optional<Role> existingRole = roleRepository.findByRoleName(role.getRoleName());
		if (existingRole.isEmpty()) {
			// Role with the same name doesn't exist, so save the new role
			var savedRole= roleRepository.saveAndFlush(role);
			return savedRole;
		} else {
			// Role with the same name already exists, handle the duplicate case
			// You can throw an exception, log a message, or handle it as needed
			return existingRole.get();
		}
	}

	@Transactional
	public Object insertPermissionsNotAttachedToRole(@Valid PermissionsToRoleDTO rolePerm) {
		// Call the custom repository method to insert permissions not attached to the
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> resMap = new HashMap<>();
		Optional<Role> role = this.roleRepository.findById(rolePerm.getRoleId());
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(role.isPresent()) {
			var rol =  role.get();
			try {
				for (RolePermission existingRolePermission : rol.getRolePermissions()) {
	                Permission permission = existingRolePermission.getPermission();

	                // Check if the permission is still in the list to be associated with the role
	                if (!rolePerm.getPermissionIds().contains(permission.getId())) {
	                    // Permission has been removed, so delete the RolePermission record
	                    rolePermissionRepository.deleteById(existingRolePermission.getId());;
	                }
	            }
	        	for (String permissionId : rolePerm.getPermissionIds()) {
				Permission permission = this.permissionRepository.findById(permissionId).orElse(null);

				if (permission != null) {
					Optional<RolePermission> existingRolePermission = rolePermissionRepository
	                        .findByRoleAndPermission(rol, permission);
					if (existingRolePermission.isPresent()) {
                        // Update the existing RolePermission
                        RolePermission rolePermission = existingRolePermission.get();
                        rolePermission.setUser(user);
                        // Optionally, update other fields if needed
                        rolePermissionRepository.save(rolePermission);
                    }else {
                    	RolePermission rolePermission = new RolePermission();
					    rolePermission.setRole(rol);
					    rolePermission.setPermission(permission);
					    rolePermission.setUser(user);
						rolePermissionRepository.save(rolePermission);

                    }
					

	            }
				
			}
	        	map.put("messaage","Permissions assigned to role");
				map.put("success","true");
				map.put("role",role.get());
	        }catch(Exception ex) {
	        	map.put("message", "Internal server.Something went wrong");
	        	map.put("success","true");
	        	
	        }
		}else {
			map.put("message","Role not found");
			map.put("success","true");            
		}
        
		
		resMap.put("payload",map);
        return resMap;
        
	
	}
	
	@Transactional
	public void insertPermissionsNotAttachedToRole(Role role, User creator, List<String> permissionIds) {
		// Call the custom repository method to insert permissions not attached to the
		// role

		for (String permissionId : permissionIds) {
			Permission permission = this.permissionRepository.findById(permissionId).orElse(null);

			if (permission != null) {
				RolePermission rolePermission = new RolePermission();
				rolePermission.setRole(role);
				rolePermission.setPermission(permission);
				rolePermission.setUser(creator);
				
//				this.rolePermissionRepository

				rolePermissionRepository.save(rolePermission);
			}
			// Handle the case where the permission with the provided ID does not exist.
		}

	}


	
	public Object getAllRoles() {
		
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> resMap =  new HashMap<>();
		try {
			List<Role> role = this.roleRepository.findAll();
			map.put("success","true");
			map.put("message","Request successful");
			map.put("roles",role);
			resMap.put("payload",map);
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}catch(Exception ex) {
			map.put("success","false");
			map.put("message","Request failed");
			resMap.put("payload",map);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resMap);
		}
			

	}
	
	public Object getUserPermissions() {
		Map<String,Object> map =  new HashMap<>();
		Map<String,Object> resMap  =  new HashMap<>();
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		try {
			 var role =  user.getUserRole().getRole();
			 if(role !=null) {
				 var permissions = this.rolePermissionRepository.findPermissionsByRole(role);
				 map.put("message","Request successful");
				 map.put("success", "true");
				 map.put("permissions",permissions);
				 resMap.put("payload",map);
			  return ResponseEntity.status(HttpStatus.OK).body(resMap);
			 }else {
				 map.put("message","No user role");
				 map.put("success", "true");
				 resMap.put("payload",map);
				 return ResponseEntity.status(HttpStatus.OK).body(resMap);
			 }
             
		}catch(Exception ex) {
			 map.put("message","Something went wrong.Internal Server Error");
			 map.put("success", "false");
			 resMap.put("payload",map);
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resMap);
		}
	}
	
	public Object getRolePermissionsByRoleId(String roleId) {
		Optional<Role> role =  this.roleRepository.findById(roleId);
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> resMap = new HashMap<>();
	  if(!role.isEmpty()) {
		  var permissions = this.rolePermissionRepository.findPermissionsByRole(role.get());
		  map.put("message","Request successful");
		  map.put("success", "true");
		  map.put("permissions",permissions);
		  resMap.put("payload",map);
		  return ResponseEntity.status(HttpStatus.OK).body(resMap);
	  }else {
		  map.put("message","Role Not Found");
		  map.put("success", "true");
		  resMap.put("payload",map);
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resMap);
   	  }
	}
	
  public Object deleteRoleByid(String roleId) {
	   return null;
  }
  


	
  

}
