package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.sasakonnect.wallet.domain.AppVersions;

public interface AppVersionsRepository extends JpaRepository<AppVersions,String> {
	@Query("SELECT av FROM AppVersions av WHERE av.version = " +
			"(SELECT MAX(av2.version) FROM AppVersions av2)")
	Optional<AppVersions> findMaxVersion();
}
