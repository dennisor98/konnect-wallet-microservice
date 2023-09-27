package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByMobileAndCountryCode(String mobile, int countryCode);

}
