package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String> {
	@Transactional
	@Modifying
	@Query("INSERT INTO Permission p (p.name) SELECT :name FROM Permission p WHERE NOT EXISTS (SELECT 1 FROM Permission p2 WHERE p2.name = :name)")
	void insertPermission(@Param("name") String name);
}
