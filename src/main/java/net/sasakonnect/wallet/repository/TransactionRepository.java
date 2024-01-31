package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

	@Query("SELECT t FROM Transaction t WHERE t.txId = :txId")
	Optional<Transaction> findByTxId(@Param("txId") String txId);
	@Query(nativeQuery = true, value = "SELECT DISTINCT account1 as account, account2 as name, u.id " + "FROM ( "
			+ "    SELECT " + "        CASE " + "            WHEN t.account_id = :accountId THEN t.oppo_account_id "
			+ "            ELSE t.account_id " + "        END AS account1, " + "        CASE "
			+ "            WHEN t.account_id = :accountId THEN t.oppo_account_name "
			+ "            ELSE t.account_name " + "        END AS account2 " + "    FROM transaction t "
			+ "    WHERE t.account_id = :accountId OR t.oppo_account_id = 46012000014325 " + "    AND ( " + "        ( "
			+ "            CASE " + "                WHEN t.account_id = :accountId THEN t.oppo_account_name "
			+ "                ELSE t.account_name " + "            END IS NOT NULL " + "            AND CASE "
			+ "                WHEN t.account_id = :accountId THEN t.oppo_account_name "
			+ "                ELSE t.account_name " + "            END != 'string' " + "        ) " + "        OR ( "
			+ "            CASE " + "                WHEN t.account_id = :accountId THEN t.oppo_account_id "
			+ "                ELSE t.account_id " + "            END IS NOT NULL " + "        ) " + "    ) "
			+ ") AS interactions " + "INNER JOIN wallet w ON w.account_id = interactions.account1 "
			+ "INNER JOIN user_wallet uw ON uw.wallet_id = w.id " + "INNER JOIN `user` u ON uw.user_id = u.id "
			+ "WHERE (account1 IS NOT NULL OR account2 IS NOT NULL) "
			+ "AND (account1 IS NOT NULL AND account2 IS NOT NULL) "
			+ "AND (account1 != 'string' AND account2 != 'string')")
	List<Object[]> findDistinctInteractions(@Param("accountId") String accountId);
	
	@Query("SELECT t FROM Transaction t WHERE t.accountId =:accountId OR t.oppoAccountId =:accountId ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountId(@Param("accountId") String accountId,Pageable pageable);
	
	@Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC LIMIT 100")
	List<Transaction> findRecentTransactions();

}
