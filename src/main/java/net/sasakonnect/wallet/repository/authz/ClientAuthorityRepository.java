package net.sasakonnect.wallet.repository.authz;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.authz.ClientAuthority;
import net.sasakonnect.wallet.domain.authz.GlobalAuthority;

public interface ClientAuthorityRepository extends JpaRepository<ClientAuthority,String> {
  Optional<ClientAuthority> findByAuthorityAndClient(GlobalAuthority auth,WalletClient client);
  
  List<ClientAuthority> findByClient(WalletClient client);
}
