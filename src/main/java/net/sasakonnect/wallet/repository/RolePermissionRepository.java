package net.sasakonnect.wallet.repository;

import java.util.List;

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
	
	@Query("SELECT p FROM Permission p JOIN RolePermission rp ON p.id = rp.permission.id WHERE rp.role.id = :roleId")
	List<Permission> findPermissionsByRole(@Param("roleId") Role role);

}
