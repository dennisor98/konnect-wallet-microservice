package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.authorisation.SmePermissions;

public interface SmePermissionRepository extends JpaRepository<SmePermissions,String> {
  Optional<SmePermissions> findByName(String name);
}
