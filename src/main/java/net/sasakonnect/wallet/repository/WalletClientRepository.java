package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Query("SELECT wc FROM WalletClient wc JOIN wc.walletClientAccount wca WHERE wc.id = :id AND wca.id IN :accountId")
	Optional<WalletClient> findByAppIdAndClientAccountId(@Param("id") String id, @Param("accountId") String accountId);

	Page<WalletClient> findAllByOrderByUpdatedAtDesc(Pageable pageable);

	@Query("SELECT wc FROM WalletClient wc " + "LEFT JOIN FETCH wc.walletClientAccount wca "
			+ "LEFT JOIN FETCH wca.smeAccount sa " + "LEFT JOIN FETCH sa.sme s "
			+ "LEFT JOIN FETCH s.accountDetails sad " + "WHERE wc.appKey = :key AND wc.enabled = true "
			+ "ORDER BY wc.createdAt DESC")
	Optional<List<WalletClient>> findWalletClientByAppKey(@Param("key") String client_app_key);

}
