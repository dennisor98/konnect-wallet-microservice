package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
	@Query("SELECT u FROM User u WHERE u.mobile = :mobile AND u.countryCode=:country_code ")
	Optional<User> findByMobileAndCountryCode(@Param("mobile") String mobile, @Param("country_code") int countryCode);

	@Query("SELECT u FROM User u JOIN FETCH u.userWallets uw JOIN FETCH uw.wallet WHERE u.id = :userId")
	Optional<User> findUserWithUserWalletsById(@Param("userId") String userId);

	@Query("SELECT u FROM User u JOIN u.userWallets uw WHERE uw.wallet.accountId = :accountId")
	Optional<User> findUserByWalletAccountId(@Param("accountId") String accountId);;

	@Transactional
	@Modifying
	@Query("DELETE FROM User u WHERE u.onboardingRequestId = :onboardingRequestId")
	void deleteByOnboardingRequestId(@Param("onboardingRequestId") String onboardingRequestId);

	@Query("SELECT u FROM User u WHERE u.onboardingRequestId = :onboardingRequestId")
	Optional<User> findByOnboardingRequestId(@Param("onboardingRequestId") String onboardingRequestId);

	Optional<User> findByMobile(String mobile); // Return an Optional<User>

}
