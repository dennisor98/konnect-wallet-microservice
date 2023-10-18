package net.sasakonnect.wallet.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserPin;

public interface UserPinRepository extends JpaRepository<UserPin, String> {
	@Query("SELECT u FROM User u JOIN FETCH u.userWallets uw JOIN FETCH uw.wallet WHERE u.id = :userId")

	Optional<User> findUserWithUserWalletsById(@Param("userId") String userId);

	@Query("SELECT up FROM UserPin up WHERE up.user = :user AND up.deletedAt IS  NULL ")
	Optional<List<UserPin>> getUserPinThatIsNotArchived(@Param("user") User user);

	@Query("SELECT u FROM UserPin u WHERE u.user.id = :userId AND u.createdAt >= :startDate")

	Optional<List<UserPin>> findPinsUsedWithinLastThreeMonths(@Param("userId") String String,
			@Param("startDate") Date startDate);

	@Modifying
	@Query("UPDATE UserPin up SET up.deletedAt = current_timestamp() WHERE up.id = :pinId")
	void markUserPinAsDeleted(@Param("pinId") String pinId);

	@Modifying
	@Transactional
	@Query("UPDATE UserPin up SET up.pinAttempts = up.pinAttempts + 1 WHERE up.user = :user AND up.deletedAt IS NULL")
	void incrementPinAttempts(@Param("user") User user);

	@Modifying
	@Transactional
	@Query("UPDATE UserPin up SET up.pinAttempts = 0 WHERE up.user = :user AND up.deletedAt IS NULL")
	void resetPinAttempts(@Param("user") User user);

	void deletAllWhereUser_Id(String id);

}
