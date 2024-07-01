package net.sasakonnect.wallet.repository.sme;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRolePermission;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;

public interface SmeRolePermissionRepository extends JpaRepository<SmeRolePermission,String>{
 Page<SmeRolePermission> findAllBySmeRole(SmeRole smeUserRole,Pageable pageable);
 @Query("SELECT rp.smePermission FROM SmeRolePermission rp "
			+ "WHERE rp.smeRole = :role AND rp.smePermission.name = :permissionName")
 Optional<SmePermissions> findBySmeRoleAndSmePermissions(@Param("role") SmeRole role,@Param("permissionName") String permissionName);

}
