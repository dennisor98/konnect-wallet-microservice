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

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountPermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;
import net.sasakonnect.wallet.repository.sme.SmeAccountPermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmePermissionRepository;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

@Service
public class SmePermissionService {
  @Autowired
  SmePermissionRepository smePermissionRepository;
  @Autowired
  SmeAccountPermissionRepository smeAccountPermissionRepository;
  
  @Transactional
	public SmeAccountPermissions insertAccountPermissionIfNotExistsOrUpdateDescription(SmeAccountPermissions permission) {
		Optional<SmeAccountPermissions> existingPermission = smeAccountPermissionRepository.findByName(permission.getName());

		if (existingPermission.isPresent()) {
			// Permission with the same name exists, update its description
			SmeAccountPermissions permissionToUpdate = existingPermission.get();
			permissionToUpdate.setDescription(permission.getDescription());
			return this.smeAccountPermissionRepository.save(permissionToUpdate);
		} else {
			// Permission does not exist, save the provided permission
			return this.smeAccountPermissionRepository.save(permission);
		}
	}
  
  public SmePermissions insertPermissionIfNotExistsOrUpdateDescription(SmePermissions permission) {
		Optional<SmePermissions> existingPermission = smePermissionRepository.findByName(permission.getName());

		if (existingPermission.isPresent()) {
			// Permission with the same name exists, update its description
			SmePermissions permissionToUpdate = existingPermission.get();
			permissionToUpdate.setDescription(permission.getDescription());
			return this.smePermissionRepository.save(permissionToUpdate);
		} else {
			// Permission does not exist, save the provided permission
			return this.smePermissionRepository.save(permission);
		}
	}
  
  
  public ResponseEntity<Object> getPermissions(){
	  List<SmePermissions> permissionsPage = this.smePermissionRepository.findAll();
	  if(permissionsPage.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request complete");
		  map.put("permissions",new ArrayList<>());
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }else {
		  var permissions = permissionsPage.stream().map(permission->{
			  Map<String,Object> pmap = new HashMap<>();
			  pmap.put("id", permission.getId());
			  pmap.put("name",permission.getName());
			  pmap.put("description", permission.getDescription());
			  pmap.put("category", permission.getCategory());
			  return pmap;
		  }).collect(Collectors.toList());
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request complete");
		  map.put("permissions", permissions);
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
	  
  }
  
  public ResponseEntity<Object> getPermissionsByCategoryName(String categoryName){
	  List<SmePermissions> permissionsPage = this.smePermissionRepository.findByCategory(categoryName);
	  if(permissionsPage.isEmpty()) {
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request complete");
		  map.put("permissions",new ArrayList<>());
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }else {
		  var permissions = permissionsPage.stream().map(permission->{
			  Map<String,Object> pmap = new HashMap<>();
			  pmap.put("id", permission.getId());
			  pmap.put("name",permission.getName());
			  pmap.put("description", permission.getDescription());
			  pmap.put("category", permission.getCategory());
			  return pmap;
		  }).collect(Collectors.toList());
		  Map<String,Object> map = new HashMap<>();
		  map.put("success",true);
		  map.put("message","Request complete");
		  map.put("permissions", permissions);
		  return ResponseEntity.status(HttpStatus.OK).body(map);
	  }
  } 
  
  public ResponseEntity<Object> getPermissionCategories(String categoryName,Integer pageNumber,Integer pageSize){
	  return null;
  } 
  
  
  public List<SmePermissions> findAll(){
	 return this.smePermissionRepository.findAll(); 
  }
  
  
}
