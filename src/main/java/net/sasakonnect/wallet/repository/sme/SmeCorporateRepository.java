package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;

public interface SmeCorporateRepository extends JpaRepository<SmeCorporate,String> {
  Optional<SmeCorporate> findSmeCorporateByUser(User user);
}
