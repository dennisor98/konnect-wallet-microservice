package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.SmeCorporate;
import net.sasakonnect.wallet.domain.sme.SmeMember;
import net.sasakonnect.wallet.domain.sme.SmePassword;

public interface SmePasswordRepository extends JpaRepository<SmePassword,String> {
	
	@Query("SELECT p FROM SmePassword p WHERE p.corporate_id = :member_id")
     Optional<SmePassword> findSmePasswordBySmeCorporaterId(@Param("member_id") SmeCorporate member_id);
	 	
}
