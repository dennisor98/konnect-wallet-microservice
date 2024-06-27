package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.Sme;

public interface SmeRepository extends JpaRepository<Sme, String> {
	@Query("SELECT sa FROM Sme sa  WHERE sa.onboardingRequestId = :onboardingRequestId")
	Optional<Sme> findSmeByOnboardingId(@Param("onboardingRequestId") String onboardingRequestId);

	@Query("SELECT sa FROM Sme sa  WHERE sa.id = :id")
	Optional<Sme> findSmeById(@Param("id") String id);
	
	Optional<Sme> findSmeByMobile(@Param("id") String id);
	

}
