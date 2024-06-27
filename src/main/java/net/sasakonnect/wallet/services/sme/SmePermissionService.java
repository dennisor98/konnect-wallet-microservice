package net.sasakonnect.wallet.services.sme;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;
import net.sasakonnect.wallet.repository.sme.SmePermissionRepository;

@Service
public class SmePermissionService {
  @Autowired
  SmePermissionRepository smePermissionRepository;
  
  @Transactional
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
}
