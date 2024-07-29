package net.sasakonnect.wallet.repository.sme;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.SmeCorporate;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;

public interface SmeUserRoleRepository extends JpaRepository<SmeUserRole,String>{
  Optional<SmeUserRole> findByUser(SmeCorporate smeCorporate);
  Optional<SmeUserRole> findBySmeRoleAndUser(SmeCorporate smeCorporate,SmeRole smeRole);
  List<SmeUserRole> findBySmeRole(SmeRole smeRole); 
}
