package net.sasakonnect.wallet.repository.sme;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.Enterprise;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;

public interface SmeRoleRepository extends JpaRepository<SmeRole,String>{
 Optional<SmeRole> findByRoleNameAndEnterprise(String roleName,Enterprise enterprise);
 
 List<SmeRole> findAllByRoleName(String name);
}
