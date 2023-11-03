package net.sasakonnect.wallet.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
			return roleRepository.save(role);
		} else {
			// Role with the same name already exists, handle the duplicate case
			// You can throw an exception, log a message, or handle it as needed
			return existingRole.get();
		}
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

				rolePermissionRepository.save(rolePermission);
			}
			// Handle the case where the permission with the provided ID does not exist.
		}

	}

}
