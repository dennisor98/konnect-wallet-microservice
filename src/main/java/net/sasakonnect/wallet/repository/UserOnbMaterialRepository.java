package net.sasakonnect.wallet.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserKycDoc;
import net.sasakonnect.wallet.domain.UserOnbMaterial;

public interface UserOnbMaterialRepository extends JpaRepository<UserOnbMaterial,String> {
	 @Query("SELECT d FROM UserOnbMaterial d WHERE d.user =:user AND d.createdAt BETWEEN :startDate AND :endDate ORDER BY d.createdAt DESC")
	  Page<UserOnbMaterial> findKycDocByUserAndDateRange(@Param("user") User user,@Param("startDate") Date startDate,@Param("endDate") Date endDate,Pageable pageable);
	 
	 @Query("SELECT d FROM UserKycDoc d WHERE d.user =:user ORDER BY d.createdAt DESC")
	 Page<UserOnbMaterial> findKycDocByUser(@Param("user") User user,Pageable pageable);
	 
	 
	 @Query("SELECT d FROM UserOnbMaterial d WHERE d.idNumber =:idNumber AND d.createdAt BETWEEN :startDate AND :endDate ORDER BY d.createdAt DESC")
	  Page<UserOnbMaterial> findKycDocByIdNumberAndDateRange(@Param("idNumber") String idNumber,@Param("startDate") Date startDate,@Param("endDate") Date endDate,Pageable pageable);
	 
	 @Query("SELECT d FROM UserKycDoc d WHERE d.idNumber =:idNumber ORDER BY d.createdAt DESC")
	 Page<UserOnbMaterial> findKycDocByIdNumber(@Param("idNumber") String idNumber,Pageable pageable);
}
