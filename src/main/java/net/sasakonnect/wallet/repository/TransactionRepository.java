package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

	@Query("SELECT t FROM Transaction t WHERE t.txId = :txId")
	Optional<Transaction> findByTxId(@Param("txId") String txId);

	@Query(nativeQuery = true, value = "SELECT DISTINCT " + "CASE "
			+ "   WHEN t.account_id = :accountId THEN t.oppo_account_id " + "   ELSE t.account_id " + "END AS account, "
			+ "CASE " + "   WHEN t.account_id = :accountId THEN t.oppo_account_name " + "   ELSE t.account_name "
			+ "END AS name " + "FROM transaction t "
			+ "WHERE (t.account_id = :accountId OR t.oppo_account_id = :accountId) " + "AND (("
			+ "   (t.account_id = :accountId AND t.oppo_account_name IS NOT NULL AND t.oppo_account_name != 'string') "
			+ "   OR "
			+ "   (t.oppo_account_id = :accountId AND t.account_name IS NOT NULL AND t.account_name != 'string')"
			+ "))")
	List<Object[]> findDistinctInteractions(@Param("accountId") String accountId);
}
