package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

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

	@Query("SELECT u FROM User u WHERE u.mobile = :mobile AND u.countryCode=:country_code ")
	Optional<User> findByMobileAndCountryCode(@Param("mobile") String mobile, @Param("country_code") int countryCode);

	@Query("SELECT u FROM User u JOIN FETCH u.userWallets uw JOIN FETCH uw.wallet WHERE u.id = :userId")
	Optional<User> findUserWithUserWalletsById(@Param("userId") String userId);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRole ur LEFT JOIN FETCH ur.role r  LEFT JOIN FETCH u.userWallets uw LEFT JOIN FETCH uw.wallet ")
	Optional<List<User>> findAllusers();

	@Query("SELECT u FROM User u JOIN u.userWallets uw WHERE uw.wallet.accountId = :accountId")
	Optional<User> findUserByWalletAccountId(@Param("accountId") String accountId);;

	@Transactional
	@Modifying
	@Query("DELETE FROM User u WHERE u.onboardingRequestId = :onboardingRequestId")
	void deleteByOnboardingRequestId(@Param("onboardingRequestId") String onboardingRequestId);

	@Query("SELECT u FROM User u WHERE u.onboardingRequestId = :onboardingRequestId")
	Optional<User> findByOnboardingRequestId(@Param("onboardingRequestId") String onboardingRequestId);

	Optional<User> findByMobile(String mobile); // Return an Optional<User>

	@Query("SELECT u FROM User u WHERE u.openId = :open_id")

	Optional<User> findByOpenId(@Param("open_id") String open_id);

}
