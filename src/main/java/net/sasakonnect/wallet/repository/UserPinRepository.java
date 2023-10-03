package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserPin;

public interface UserPinRepository extends JpaRepository<UserPin, String> {
	@Query("SELECT u FROM User u JOIN FETCH u.userWallets uw JOIN FETCH uw.wallet WHERE u.id = :userId")

	Optional<User> findUserWithUserWalletsById(@Param("userId") String userId);

	@Query("SELECT up FROM UserPin up WHERE up.user = :user AND up.deletedAt IS NOT NULL")
	Optional<List<UserPin>> getUserPinThatIsNotArchived(@Param("user") User user);

}
