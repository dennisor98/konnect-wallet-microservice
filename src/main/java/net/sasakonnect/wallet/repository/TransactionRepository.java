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
			+ "    WHEN t.account_id = :accountId THEN t.oppo_account_id " + "    ELSE t.account_id "
			+ "END AS account1, " + "CASE " + "    WHEN t.account_id = :accountId THEN t.oppo_account_name "
			+ "    ELSE t.account_name " + "END AS account2, " + "u.id " + "FROM transaction t "
			+ "INNER JOIN wallet w ON " + "    CASE " + "        WHEN t.account_id = :accountId THEN t.oppo_account_id "
			+ "        ELSE t.account_id " + "    END = w.account_id "
			+ "INNER JOIN user_wallet uw ON uw.wallet_id = w.id " + "INNER JOIN user u ON uw.user_id = u.id " + "WHERE "
			+ "    (CASE " + "        WHEN t.account_id = :accountId THEN t.oppo_account_name "
			+ "        ELSE t.account_name " + "    END IS NOT NULL " + "    AND " + "    CASE "
			+ "        WHEN t.account_id = :accountId THEN t.oppo_account_name " + "        ELSE t.account_name "
			+ "    END != 'string') " + "    OR " + "    (CASE "
			+ "        WHEN t.account_id = :accountId THEN t.oppo_account_id " + "        ELSE t.account_id "
			+ "    END IS NOT NULL) " + "    AND " + "    ( "
			+ "        (t.account_id IS NOT NULL OR t.account_name IS NOT NULL) " + "        AND "
			+ "        (t.account_id != 'string' AND t.account_name != 'string') " + "    )")
	List<Object[]> findDistinctInteractions(@Param("accountId") String accountId);
}
