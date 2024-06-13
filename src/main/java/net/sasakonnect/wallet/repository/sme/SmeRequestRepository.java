package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.SmeRequestProceed;

public interface SmeRequestRepository extends JpaRepository<SmeRequestProceed,String> {
  Optional<SmeRequestProceed> findSmeRequestProceedByHash(String hash);
}
