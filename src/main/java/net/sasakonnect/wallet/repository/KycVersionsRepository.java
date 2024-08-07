package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.sasakonnect.wallet.domain.KycVersions;

public interface KycVersionsRepository extends JpaRepository<KycVersions,String>{
	@Query("SELECT kv FROM KycVersions kv WHERE kv.version = " +
			"(SELECT MAX(kv2.version) FROM KycVersions kv2)")
	Optional<KycVersions> findMaximumVersion();
	Optional<KycVersions> findByVersion(Integer version);
}
