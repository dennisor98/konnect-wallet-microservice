package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountPermissions;

public interface SmeAccountPermissionRepository extends JpaRepository<SmeAccountPermissions,String> {
  Optional<SmeAccountPermissions> findByName(String name);
}
