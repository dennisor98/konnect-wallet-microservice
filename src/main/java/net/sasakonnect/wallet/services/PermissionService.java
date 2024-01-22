package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.openidconnect.IdToken.Payload;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import net.sasakonnect.wallet.RequestDto.PermissionDTO;
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
	 
	 public Object editPermission(String permissionId,PermissionDTO paylod) {
		 Optional<Permission> permission = this.permissionRepository.findById(permissionId);
		 Map<String,Object> payloadMap = new HashMap<>();
		 Map<String,Object> map = new HashMap<>();
		 if(permission.isPresent()) {
			 Permission perm  = permission.get();
			 perm.setName(paylod.name);
			 perm.setDescription(paylod.description);
			 try {
				this.permissionRepository.save(perm); 
				map.put("success", "true");
				map.put("message","Request successful");
				payloadMap.put("payload",map);
				return ResponseEntity.status(HttpStatus.OK).body(payloadMap);
			 }catch(Exception ex) {
				    map.put("success", "false");
					map.put("message","Oops!Something went wrong");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
			 }
			 
			 
		 }else {
			    map.put("success", "false");
				map.put("message","Permission not found");
				payloadMap.put("payload",map);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payloadMap);
		 }
	 }
	 
	
}
