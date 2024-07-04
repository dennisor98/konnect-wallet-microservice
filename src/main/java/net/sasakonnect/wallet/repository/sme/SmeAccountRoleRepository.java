package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRole;

public interface SmeAccountRoleRepository  extends JpaRepository<SmeAccountRole,String>{
   Optional<SmeAccountRole> findByNameAndSmeAccount(String name,SmeAccount smeAccount);
}
