package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.RejectedAccount;
import net.sasakonnect.wallet.domain.User;

public interface RejectedAccountRepository extends JpaRepository<RejectedAccount,String> {
   void deleteByIdNumber(String idNumber);
   
   @Query("SELECT u FROM RejectedAccount u WHERE u.mobile =:mobile ORDER BY u.updatedAt DESC LIMIT 1")
   Optional<RejectedAccount> findByMobile(@Param("mobile") String mobile);
   
   Optional<RejectedAccount> findByIdNumber(String idNumber);
   
   @Query("SELECT u FROM RejectedAccount u   WHERE u.firstName LIKE %:queryString% "
			+ "OR u.lastName LIKE %:queryString% OR u.middleName LIKE %:queryString% OR u.idNumber LIKE  %:queryString% OR u.mobile LIKE %:queryString%")
	Page<RejectedAccount> searchUser(@Param("queryString") String queryString, Pageable pageable);
}
