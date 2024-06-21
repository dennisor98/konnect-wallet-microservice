package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.RejectedAccount;

public interface RejectedAccountRepository extends JpaRepository<RejectedAccount,String> {
   void deleteByIdNumber(String idNumber);
   
   @Query("SELECT u FROM RejectedAccount u WHERE u.mobile =:mobile ORDER BY u.updatedAt DESC LIMIT 1")
   Optional<RejectedAccount> findByMobile(@Param("mobile") String mobile);
}
