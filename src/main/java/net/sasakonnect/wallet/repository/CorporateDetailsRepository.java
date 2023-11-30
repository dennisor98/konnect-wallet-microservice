package net.sasakonnect.wallet.repository;
import jakarta.persistence.EntityManager;
import java.util.logging.*;
import jakarta.persistence.PersistenceContext;
import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CorporateDetailsRepository extends JpaRepository<CorporateDetails,String>{	
	Optional<CorporateDetails> findCorporateDetailsByCorporateEmail(@Param("corporate_email") String email);
    
}
