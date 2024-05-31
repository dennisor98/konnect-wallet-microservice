package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.SmeAccount;

public interface SmeAccountRepository extends JpaRepository<SmeAccount, String> {
	@Query("SELECT sa FROM SmeAccount sa JOIN FETCH sa.enterprise")
	Page<SmeAccount> findAllWithEnterprise(Pageable pageable);

	@Query("SELECT sa FROM SmeAccount sa JOIN FETCH sa.enterprise WHERE sa.id = :id")
	Page<SmeAccount> findAllWithEnterpriseById(@Param("id") String id, Pageable pageable);

	@Query("SELECT sa FROM SmeAccount sa  WHERE sa.onboardingRequestId = :onboardingRequestId")
	Optional<SmeAccount> findSmeByOnboardingId(@Param("onboardingRequestId") String onboardingRequestId);

	@Query("SELECT sa FROM SmeAccount sa  WHERE sa.id = :id")
	Optional<SmeAccount> findSmeById(@Param("id") String id);
}
