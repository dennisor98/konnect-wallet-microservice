package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String> {
	Optional<Permission> findByName(String name);

}
