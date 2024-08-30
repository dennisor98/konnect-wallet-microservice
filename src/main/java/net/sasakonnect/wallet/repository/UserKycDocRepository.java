package net.sasakonnect.wallet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserKycDoc;

public interface UserKycDocRepository extends JpaRepository<UserKycDoc,String> {
	@Query("SELECT d FROM UserKycDoc d WHERE d.user =:user ORDER BY d.createdAt DESC")
	Page<UserKycDoc> findKycDocByUser(@Param("user") User user,Pageable pageable);
	
  @Query("SELECT d FROM UserKycDoc d WHERE d.user =:user AND d.createdAt BETWEEN :startDate AND :endDate ORDER BY d.createdAt DESC")
  Page<UserKycDoc> findKycDocByUserAndDateRange(@Param("user") User user,@Param("startDate") Date startDate,@Param("endDate") Date endDate,Pageable pageable);
}
