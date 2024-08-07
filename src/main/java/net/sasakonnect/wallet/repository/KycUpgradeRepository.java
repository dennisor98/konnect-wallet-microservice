package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.KycUpgrade;
import net.sasakonnect.wallet.domain.User;

public interface KycUpgradeRepository extends JpaRepository<KycUpgrade,String>{
	@Query("SELECT ku FROM KycUpgrade ku WHERE ku.user = :user AND ku.version = " +
			"(SELECT MAX(ku2.version) FROM KycUpgrade ku2 WHERE ku2.user = :user)")
	Optional<KycUpgrade> findMaxVersionByUser(@Param("user") User user);
}
