package net.sasakonnect.wallet.repository.sme;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;

public interface SmeUserRoleRepository extends JpaRepository<SmeUserRole,String>{

}
