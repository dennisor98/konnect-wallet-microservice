package net.sasakonnect.wallet.repository.sme;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;

public interface SmeCorporateRepository extends JpaRepository<SmeCorporate,String> {
  Optional<SmeCorporate> findSmeCorporateByUserAndSmes(User user,Sme sme);
  List<SmeCorporate> findSmeCorporateByUser(User user);
  @Query("SELECT c.smes FROM SmeCorporate c WHERE c.user =:user")
  List<Sme> findSmesByUser(@Param("user") User user);
}
