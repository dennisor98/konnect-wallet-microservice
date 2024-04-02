package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

//	get all users
//	@Query("SELECT u  FROM User u")
//	 Optional<List<User>> findAllUsers();	
	// get corporate users
	@Query("SELECT u FROM User u WHERE u.corporate IS NOT NULL")
	Optional<List<User>> getCorporateUsers();

	@Query("SELECT u FROM User u WHERE u.corporate = :corporateId")
	Optional<User> getUserByCorporateId(@Param("corporateId") CorporateDetails corporateId);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userWallets uw LEFT JOIN FETCH uw.wallet WHERE u.firstName LIKE %:queryString% "
			+ "OR u.lastName LIKE %:queryString% OR uw.wallet.accountId LIKE %:queryString% OR u.mobile LIKE %:queryString%")
	Page<User> searchUser(@Param("queryString") String queryString, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.mobile = :mobile AND u.countryCode=:country_code ")
	Optional<User> findByMobileAndCountryCode(@Param("mobile") String mobile, @Param("country_code") int countryCode);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage JOIN FETCH u.userWallets uw JOIN FETCH uw.wallet WHERE u.id = :userId")
	Optional<User> findUserWithUserWalletsById(@Param("userId") String userId);

	@Query("SELECT u FROM User u  LEFT JOIN FETCH u.userWallets uw  LEFT JOIN FETCH uw.wallet WHERE u.id = :userId")
	Optional<User> findUserWithWalletsById(@Param("userId") String userId);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRole ur LEFT JOIN FETCH ur.role r  LEFT JOIN FETCH u.userWallets uw LEFT JOIN FETCH uw.wallet ")
	Page<User> findAllusers(Pageable page);

	@Query("SELECT u FROM User u JOIN u.userWallets uw WHERE uw.wallet.accountId = :accountId")
	Optional<User> findUserByWalletAccountId(@Param("accountId") String accountId);;

	@Transactional
	@Modifying
	@Query("DELETE FROM User u WHERE u.onboardingRequestId = :onboardingRequestId")
	void deleteByOnboardingRequestId(@Param("onboardingRequestId") String onboardingRequestId);

	@Query("SELECT u FROM User u WHERE u.onboardingRequestId = :onboardingRequestId")
	Optional<User> findByOnboardingRequestId(@Param("onboardingRequestId") String onboardingRequestId);

	@Query("SELECT u FROM User u  LEFT JOIN FETCH u.userWallets uw LEFT JOIN FETCH uw.wallet WHERE u.mobile =:mobileNumber")
	Optional<User> findByMobile(@Param("mobileNumber") String mobile); // Return an Optional<User>

	@Query("SELECT u FROM User u WHERE u.openId = :open_id")
	Optional<User> findByOpenId(@Param("open_id") String open_id);
	
	@Query("SELECT DATE(u.createdAt),COUNT(u) FROM User u  WHERE MONTH(u.createdAt) =:month AND YEAR(u.createdAt) =:year GROUP BY DATE(u.createdAt) ORDER BY DATE(u.createdAt) DESC")
	List<Object[]> findDailyOnBoardingTrend(@Param("month") int month,@Param("year") int year);
	
	@Query("SELECT MONTHNAME(u.createdAt),COUNT(u) FROM User u WHERE  YEAR(u.createdAt) =:year GROUP BY MONTHNAME(u.createdAt),MONTH(u.createdAt) ORDER BY MONTH(u.createdAt) DESC")
	List<Object[]> findMonthlyOnBoardingTrend(@Param("year") int year);
	
	
	@Query("SELECT YEAR(u.createdAt),COUNT(u) FROM User u GROUP BY YEAR(u.createdAt) ORDER BY YEAR(u.createdAt) DESC")
     List<Object[]> findAnnualOnBoardingTrend();
     
//     @Query("SELECT COUNT(*) FROM User u WHERE DATE(u.createdAt) <= CURRENT_DATE()")
     @Query(value = "SELECT "
    	       + "(SELECT COUNT(*) FROM user u WHERE DATE(u.created_at) <= DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY)), "
    	       + "(SELECT COUNT(*) FROM user u WHERE DATE(u.created_at) <= CURRENT_DATE())", 
    	       nativeQuery = true)
     List<Object[]> findOnBoardingDeviation();
}
