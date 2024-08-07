package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.FirebaseToken;
import net.sasakonnect.wallet.domain.User;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, String> {
      void deleteByUser(User user);
}
