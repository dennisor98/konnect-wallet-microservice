package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRole;

public interface SmeAccountRoleRepository  extends JpaRepository<SmeAccountRole,String>{
   Optional<SmeAccountRole> findByNameAndSmeAccount(String name,SmeAccount smeAccount);
   
   @Query("SELECT r FROM SmeAccountRole r WHERE r.smeAccount.sme =:sme")
   Page<SmeAccountRole> findBySme(@Param("sme") Sme sme,Pageable pageable);
}
