package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeAccountUserRole;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;

public interface SmeAccountUserRoleRepository extends JpaRepository<SmeAccountUserRole,String> {
	
   Optional<SmeAccountUserRole> findBySmeCorporate(SmeCorporate corporate);
   @Query("SELECT scr FROM SmeAccountUserRole scr WHERE scr.smeCorporate =:corporate AND scr.role.smeAccount =:smeaccount")
   Optional<SmeAccountUserRole> findByCorporateAndSmeAccount(@Param("corporate") SmeCorporate corporate,@Param("smeaccount") SmeAccount smeaccount);
}
