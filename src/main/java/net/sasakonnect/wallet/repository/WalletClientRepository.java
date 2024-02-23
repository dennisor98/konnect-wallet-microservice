package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.WalletClient;

public interface WalletClientRepository extends JpaRepository<WalletClient, String> {
	@Query("SELECT wc FROM WalletClient wc WHERE wc.appKey = :key AND wc.appSecret = :secret AND wc.enabled = true ORDER BY wc.createdAt DESC")
	Optional<List<WalletClient>> findByAppKeyAndAppSecret(@Param("key") String key, @Param("secret") String secret);

	@Query("SELECT wc FROM WalletClient wc JOIN FETCH wc.walletClientAccount WHERE wc.appKey = :key AND wc.enabled = true ORDER BY wc.createdAt DESC")
	Optional<List<WalletClient>> findByAppKeyAnd(@Param("key") String client_app_key);

	@Query("SELECT wc FROM WalletClient wc WHERE  wc.appSecret = :secret AND wc.enabled = true ORDER BY wc.createdAt DESC")
	Optional<List<WalletClient>> findByAppSecret(@Param("secret") String secret);

}
