package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountPermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRolePermission;

public interface SmeAccountRolePermissionRepository extends JpaRepository<SmeAccountRolePermission,String>{
	@Query("SELECT ap FROM SmeAccountRolePermission ap WHERE ap.permission.name =:permission  AND ap.role =:role")
   Optional<SmeAccountRolePermission> findRolePermissionByRoleAndPermission(@Param("role")SmeAccountRole role,@Param("permission")String permission);
   
}
