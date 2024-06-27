package net.sasakonnect.wallet.repository.sme;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.authorisation.SmeRolePermission;

public interface SmeRolePermissionRepository extends JpaRepository<SmeRolePermission,String>{

}
