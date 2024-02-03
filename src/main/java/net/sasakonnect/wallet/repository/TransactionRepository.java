package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.RequestDto.admin.WalletRank;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.Wallet;

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
	
	@Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC")
	Page<Transaction> findRecentTransactions(Pageable pageable);
	
	@Query("SELECT COUNT(t) FROM Transaction t WHERE t.txStatus = 8 AND t.oppoAccountId =:accountId")
	Long findAllReceived(@Param("accountId") String accountId);
	
	@Query("SELECT COUNT(t)  FROM Transaction t WHERE t.txStatus = 8 AND t.accountId =:accountId ")
	Long findAllSent(@Param("accountId") String accountId);
	
	@Query("SELECT COUNT(t) FROM Transaction t WHERE (t.accountId =:accountId OR t.oppoAccountId =:accountId) AND t.txStatus = 8 ")
	Long findCountByAccountId(@Param("accountId") String accountId);
	
	@Query(value="SELECT DISTINCT CONCAT(u.first_name, ' ', u.last_name) AS name, w.account_id, COALESCE(ts.transaction_count, 0) AS transaction_count, COALESCE(ts.totalAmount, 0) AS totalAmount FROM (SELECT account_id, COUNT(*) AS transaction_count, SUM(ABS(amount)) AS totalAmount FROM transaction WHERE tx_status = 8 GROUP BY account_id) ts LEFT JOIN wallet w ON ts.account_id = w.account_id LEFT JOIN user_wallet uw ON uw.wallet_id = w.id LEFT JOIN (SELECT id, first_name, last_name FROM user) AS u ON u.id = uw.user_id GROUP BY w.account_id, name, transaction_count, totalAmount ORDER BY totalAmount DESC LIMIT :pageSize OFFSET :pageNumber",nativeQuery=true)
	List<Object[]> findWalletRank(@Param("pageNumber") Integer pageNumber,@Param("pageSize") Integer pageSize);
	
	@Query("SELECT DATE(t.createdAt) as date_created,COUNT(*) total_transactions,SUM(ABS(t.amount)) as total_transacted FROM Transaction t WHERE MONTH(t.createdAt) =:month AND YEAR(t.createdAt) =:year AND t.txStatus = 8  GROUP BY DATE(createdAt) ORDER BY DATE(createdAt)ASC")
	List<Object[]> findTransactionTrend(@Param("month") Integer month,@Param("year") Integer yaer);
	
	@Query("SELECT MONTHNAME(t.createdAt) as date_created,COUNT(*) total_transactions,SUM(ABS(t.amount)) as total_transacted FROM Transaction t WHERE YEAR(t.createdAt) =:year AND t.txStatus = 8 GROUP BY MONTHNAME(createdAt),MONTH(createdAt) ORDER BY MONTH(createdAt)ASC")
	List<Object[]> findMonthlyTransactionTrend(@Param("year") Integer yaer);
	
	@Query("SELECT YEAR(t.createdAt) as date_created,COUNT(*) total_transactions,SUM(ABS(t.amount)) as total_transacted FROM Transaction t WHERE t.txStatus = 8 GROUP BY YEAR(createdAt)  ORDER BY YEAR(createdAt)ASC")
	List<Object[]> findAnnualTransactionTrend();
	
	

}
