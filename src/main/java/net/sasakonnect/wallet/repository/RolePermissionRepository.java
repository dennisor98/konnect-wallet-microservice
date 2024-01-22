package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
	@Query("SELECT rp.permission FROM RolePermission rp "
			+ "WHERE rp.role = :role AND rp.permission.name = :permissionName")
	List<Permission> findPermissionsByRoleAndPermissionName(@Param("role") Role role,
			@Param("permissionName") String permissionName);
	
	@Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role =:role")
	List<Permission> findPermissionsByRole(@Param("role") Role role);

	@Query("SELECT rp FROM RolePermission rp WHERE rp.role =:rol AND rp.permission=:permission ")
	Optional<RolePermission> findByRoleAndPermission(Role rol, Permission permission);
	

}
