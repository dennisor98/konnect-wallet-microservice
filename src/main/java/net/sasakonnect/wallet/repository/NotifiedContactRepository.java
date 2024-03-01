package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.NotifiedContact;


public interface NotifiedContactRepository extends JpaRepository<NotifiedContact, String> {
	@Query("SELECT c FROM NotifiedContact c WHERE c.contact =:contact ")
    Optional<NotifiedContact> findByContact(@Param("contact") String contact);
}
