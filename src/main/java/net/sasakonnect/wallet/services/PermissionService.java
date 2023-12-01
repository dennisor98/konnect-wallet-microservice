package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.repository.PermissionRepository;

@Service
public class PermissionService {
	@Autowired
	private PermissionRepository permissionRepository;

	@Transactional
	public Permission insertPermissionIfNotExistsOrUpdateDescription(Permission permission) {
		Optional<Permission> existingPermission = permissionRepository.findByName(permission.getName());

		if (existingPermission.isPresent()) {
			// Permission with the same name exists, update its description
			Permission permissionToUpdate = existingPermission.get();
			permissionToUpdate.setDescription(permission.getDescription());
			return permissionRepository.save(permissionToUpdate);
		} else {
			// Permission does not exist, save the provided permission
			return permissionRepository.save(permission);
		}
	}

	public List<Permission> findAll() {
		// TODO Auto-generated method stub
		return this.permissionRepository.findAll();
	}
	
	 public Object getAllPermissions() {
			
			Map<String,Object> map = new HashMap<>();
			Map<String,Object> resMap =  new HashMap<>();
			try {
				List<Permission> permisssions = this.permissionRepository.findAll();
				map.put("success","true");
				map.put("message","Request successful");
				map.put("permissions",permisssions);
				resMap.put("payload", map);
				return ResponseEntity.status(HttpStatus.OK).body(resMap);
			}catch(Exception ex) {
				map.put("success","false");
				map.put("message","Request failed");
				resMap.put("payload", map);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);	
			}
		   
		}
}
