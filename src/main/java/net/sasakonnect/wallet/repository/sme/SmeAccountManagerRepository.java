package net.sasakonnect.wallet.repository.sme;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeAccountManager;

public interface SmeAccountManagerRepository extends JpaRepository<SmeAccountManager,String>{
  Optional<SmeAccountManager> findBySmeAccountAndUser(SmeAccount smeAcc,User user);
  
  
  @Query("SELECT sme.smeAccount FROM SmeAccountManager sme WHERE sme.user =:user")
  List<SmeAccount> findSmeAccountByUser(@Param("user") User user);
}
