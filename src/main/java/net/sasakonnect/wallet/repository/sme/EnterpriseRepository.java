package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.sasakonnect.wallet.domain.sme.Enterprise;

public interface EnterpriseRepository extends JpaRepository<Enterprise, String> {
	@Query("SELECT e FROM Enterprise e JOIN FETCH e.creator")
	Page<Enterprise> findAllWithCreator(Pageable pageable);

	Optional<Enterprise> findByName(String name);

}
