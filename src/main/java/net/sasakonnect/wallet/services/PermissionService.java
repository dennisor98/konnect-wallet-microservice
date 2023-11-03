package net.sasakonnect.wallet.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
}
