package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.ClientAppScopes;
import net.sasakonnect.wallet.domain.WalletClient;

public interface ClientAppScopesRepository extends JpaRepository<ClientAppScopes,String>{
  Optional<ClientAppScopes> findByClientAndAppKey(WalletClient client,String appKey);
}
