package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.FinancialInstituation;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.WalletClientAccount;

public interface WalletClientAccountRepository extends JpaRepository<WalletClientAccount, String> {
	@Query("SELECT wc FROM WalletClient wc " + "LEFT JOIN FETCH wc.walletClientAccount wca "
			+ "LEFT JOIN FETCH wc.user u " + "WHERE wca.tillNumber = :tillNumber AND wca.accountType = :accountType")
	List<WalletClient> findWalletClientByTillNumberAndAccountType(@Param("tillNumber") String tillNumber,
			@Param("accountType") FinancialInstituation accountType);

	@Query("SELECT wc FROM WalletClient wc " + "LEFT JOIN FETCH wc.walletClientAccount wca "
			+ "LEFT JOIN FETCH wc.user u " + "WHERE " + "wca.tillNumber = :account or "
			+ "wca.paybillNumber = :account  or " + "wca.walletAccountNo = :account  ")
	List<WalletClient> findWalletClientByAccount(@Param("account") String account);
	
	@Query("SELECT wa FROM WalletClientAccount wa WHERE wa.walletClient =:walletClient AND wa.isPrimary = true")
	Optional<WalletClientAccount> findPrimaryWalletClientaccount(@Param("walletClient") String client);
	
}