package net.sasakonnect.wallet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.constant.ChannelType;
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
  
	@Override
	 @Query("SELECT t FROM Transaction t ORDER BY t.updatedAt DESC")
	 Page<Transaction> findAll(Pageable page);
	
	@Query("SELECT t FROM Transaction t WHERE ((t.accountId =:accountId)  OR (t.accountId  =:accountId AND t.txType ='TTID0003'))  ORDER BY t.updatedAt DESC")
	Page<Transaction> findByAccountId(@Param("accountId") String accountId, Pageable pageable);
	
	@Query("SELECT t FROM Transaction t WHERE ((t.accountId =:accountId)  OR (t.accountId  =:accountId AND t.txType ='TTID0003')) AND t.txStatus != -1 ORDER BY t.updatedAt DESC")
	Page<Transaction> findByUserAccountId(@Param("accountId") String accountId, Pageable pageable);

	@Query("SELECT t FROM Transaction t ORDER BY t.updatedAt DESC")
	Page<Transaction> findRecentTransactions(Pageable pageable);

	@Query("SELECT COUNT(t) FROM Transaction t WHERE t.txStatus = 8 AND t.oppoAccountId =:accountId")
	Long findAllReceived(@Param("accountId") String accountId);

	@Query("SELECT COUNT(t)  FROM Transaction t WHERE t.txStatus = 8 AND t.accountId =:accountId ")
	Long findAllSent(@Param("accountId") String accountId);

	@Query("SELECT COUNT(t) FROM Transaction t WHERE (t.accountId =:accountId OR t.oppoAccountId =:accountId) AND t.txStatus = 8 ORDER BY t.updatedAt DESC ")
	Long findCountByAccountId(@Param("accountId") String accountId);
  
	@Query("SELECT t FROM Transaction t WHERE ((LOWER(t.accountId) LIKE %:queryString%) OR (LOWER(t.accountId) LIKE %:queryString% AND t.txType ='TTID0003') OR LOWER(t.txId) LIKE %:queryString% OR LOWER(t.accountName) LIKE %:queryString%) OR LOWER(t.oppoAccountId) LIKE %:queryString% OR LOWER(t.oppoAccountName) LIKE %:queryString% ORDER BY t.createdAt DESC")	
	Page<Transaction> searchTransaction(@Param("queryString") String queryString,Pageable page);
	
	@Query(value = "SELECT DISTINCT CONCAT(u.first_name, ' ', u.last_name) AS name, w.account_id, COALESCE(ts.transaction_count, 0) AS transaction_count, COALESCE(ts.totalAmount, 0) AS totalAmount FROM (SELECT account_id, COUNT(*) AS transaction_count, SUM(ABS(amount)) AS totalAmount FROM transaction WHERE tx_status = 8 GROUP BY account_id) ts LEFT JOIN wallet w ON ts.account_id = w.account_id LEFT JOIN user_wallet uw ON uw.wallet_id = w.id LEFT JOIN (SELECT id, first_name, last_name FROM user) AS u ON u.id = uw.user_id GROUP BY w.account_id, name, transaction_count, totalAmount ORDER BY totalAmount DESC LIMIT :pageSize OFFSET :pageNumber", nativeQuery = true)
	List<Object[]> findWalletRank(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize);

	@Query("SELECT DATE(t.createdAt) as date_created,COUNT(*) total_transactions,SUM(ABS(t.amount)) as total_transacted FROM Transaction t WHERE MONTH(t.createdAt) =:month AND YEAR(t.createdAt) =:year AND t.txStatus = 8  GROUP BY DATE(createdAt) ORDER BY DATE(createdAt)ASC")
	List<Object[]> findTransactionTrend(@Param("month") Integer month, @Param("year") Integer yaer);

	@Query("SELECT MONTHNAME(t.createdAt) as date_created,COUNT(*) total_transactions,SUM(ABS(t.amount)) as total_transacted FROM Transaction t WHERE YEAR(t.createdAt) =:year AND t.txStatus = 8 GROUP BY MONTHNAME(createdAt),MONTH(createdAt) ORDER BY MONTH(createdAt)ASC")
	List<Object[]> findMonthlyTransactionTrend(@Param("year") Integer yaer);

	@Query("SELECT YEAR(t.createdAt) as date_created,COUNT(*) total_transactions,SUM(ABS(t.amount)) as total_transacted FROM Transaction t WHERE t.txStatus = 8 GROUP BY YEAR(createdAt)  ORDER BY YEAR(createdAt)ASC")
	List<Object[]> findAnnualTransactionTrend();

	@Query("SELECT t FROM Transaction t WHERE t.txId =:txId")
	Optional<Transaction> findTransactionByTxtId(@Param("txId") String txId);
	
	@Query("SELECT IFNULL(ABS(t_out.toMpesa), 0), IFNULL(ABS(w.toWallet), 0), IFNULL(ABS(r.received), 0), IFNULL(ABS(tb.tillPaybill), 0), IFNULL(ABS(u.utility), 0) " +
		       "FROM (SELECT SUM(t.amount) as toMpesa FROM Transaction t WHERE t.amount < 0 AND t.accountId = :accountId  AND (t.txType = 'TTID0001' OR (t.txType ='TTID0002' AND t.oppoAccountId NOT IN(SELECT w.accountId FROM Wallet w))) AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS t_out, " +
		       "(SELECT SUM(t.amount) as toWallet FROM Transaction t WHERE t.amount < 0 AND t.accountId = :accountId AND t.txType = 'TTID0002' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS w, "+
		       "(SELECT SUM(t.amount) as received FROM Transaction t WHERE t.amount > 0 AND t.oppoAccountId = :accountId AND t.txType = 'TTID0003' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS r, "+
		       "(SELECT SUM(t.amount) as tillPaybill FROM Transaction t WHERE t.amount < 0 AND t.accountId = :accountId AND t.txType = 'TTID0005' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS tb, " +
		       "(SELECT SUM(t.amount) as utility FROM Transaction t WHERE t.amount < 0 AND ((t.accountId = :accountId AND t.txType = 'TTID0006') OR (t.oppoAccountId = :accountId AND t.txType = 'TTID0006')) AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS u")
	List<Object[]> findWalletTransactionBehaviour(@Param("accountId") String accountId,@Param("year") int year,@Param("month") int month);
	
	
	
	//first method overload(get general summary in a given month in a year)
	@Query("SELECT IFNULL(ABS(t_out.toMpesa), 0),t_out.cnt,IFNULL(ABS(w.toWallet), 0),w.cnt,IFNULL(ABS(tb.tillPaybill), 0),tb.cnt,IFNULL(ABS(u.utility), 0),u.cnt " +
		       "FROM (SELECT SUM(t.amount) as toMpesa,COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0  AND (t.txType = 'TTID0001' OR (t.txType = 'TTID0002' AND t.oppoAccountId NOT IN(SELECT w.accountId FROM Wallet w))) AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS t_out, " +
		       "(SELECT SUM(t.amount) as toWallet,COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0002' AND t.oppoAccountId IN(SELECT w.accountId FROM Wallet w) AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS w, "+
		       "(SELECT SUM(t.amount) as tillPaybill,COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0005' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS tb, " +
		       "(SELECT SUM(t.amount) as utility,COUNT(t) cnt FROM Transaction t WHERE t.amount < 0  AND t.txType = 'TTID0006' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year AND MONTH(t.updatedAt) = :month) AS u")
	List<Object[]> findWalletTransactionBehaviour(@Param("year") int year,@Param("month") int month);
	
	    //2nd method overload(get general summary in a given year)
	@Query("SELECT IFNULL(ABS(t_out.toMpesa), 0), t_out.cnt, IFNULL(ABS(w.toWallet), 0), w.cnt, IFNULL(ABS(tb.tillPaybill), 0), tb.cnt, IFNULL(ABS(u.utility), 0), u.cnt " +
		       "FROM (SELECT SUM(t.amount) as toMpesa,COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND (t.txType = 'TTID0001' OR (t.txType = 'TTID0002' AND t.oppoAccountId NOT IN (SELECT w.accountId FROM Wallet w))) AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year) AS t_out, " +
		       "(SELECT SUM(t.amount) as toWallet, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0002' AND t.oppoAccountId IN (SELECT w.accountId FROM Wallet w) AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year) AS w, " +
		       "(SELECT SUM(t.amount) as tillPaybill, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0005' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year) AS tb, " +
		       "(SELECT SUM(t.amount) as utility, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0006' AND t.txStatus = 8 AND YEAR(t.updatedAt) = :year) AS u")
	List<Object[]> findWalletTransactionBehaviour(@Param("year") int year);


	
	    //3rd method overload(get general summary in the system without time period)
	@Query("SELECT IFNULL(ABS(t_out.toMpesa), 0), t_out.cnt, IFNULL(ABS(w.toWallet), 0), w.cnt, IFNULL(ABS(tb.tillPaybill), 0), tb.cnt, IFNULL(ABS(u.utility), 0), u.cnt " +
		       "FROM (SELECT SUM(t.amount) as toMpesa, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND (t.txType = 'TTID0001' OR (t.txType = 'TTID0002' AND t.oppoAccountId NOT IN (SELECT w.accountId FROM Wallet w))) AND t.txStatus = 8) AS t_out, " +
		       "(SELECT SUM(t.amount) as toWallet, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0002' AND t.oppoAccountId IN (SELECT w.accountId FROM Wallet w) AND t.txStatus = 8) AS w, " +
		       "(SELECT SUM(t.amount) as tillPaybill, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0005' AND t.txStatus = 8) AS tb, " +
		       "(SELECT SUM(t.amount) as utility, COUNT(t) as cnt FROM Transaction t WHERE t.amount < 0 AND t.txType = 'TTID0006' AND t.txStatus = 8) AS u")
	List<Object[]> findWalletTransactionBehaviour();

		
	 @Query("SELECT t FROM Transaction t " +
	           "WHERE t.txType = :txType " +
	           "AND t.accountId = :accountId " +
	           "AND t.oppoAccountId IS NOT NULL " +
	           "AND t.oppoAccountId NOT IN (SELECT w.accountId FROM Wallet w) " +
	           "AND t.id = (SELECT MIN(t2.id) FROM Transaction t2 " +
	           "            WHERE t2.oppoAccountId = t.oppoAccountId " +
	           "            AND t2.txType = :txType " +
	           "            AND t2.accountId = :accountId) ")
   Page<Transaction> findRecentTransactionContactByTxType(@Param("txType") String txType,@Param("accountId") String accountId,Pageable page);
   
	 @Query("SELECT t FROM Transaction t " +
		       "WHERE t.txType = :txType " +
		       "AND t.accountId = :accountId " +
		       "AND t.oppoAccountId IS NOT NULL " +
		       "AND EXISTS (SELECT 1 FROM Wallet w WHERE w.accountId = t.oppoAccountId) " +
		       "AND t.id = (SELECT MIN(t2.id) FROM Transaction t2 " +
		       "            WHERE t2.oppoAccountId = t.oppoAccountId " +
		       "            AND t2.txType = :txType " +
		       "            AND t2.accountId = :accountId)")
   Page<Transaction> findWalletToWalletTransContact(@Param("txType") String txType,@Param("accountId") String accountId,Pageable page);
	 
   @Query("SELECT t FROM Transaction t WHERE t.txType =:channel AND ((t.createdAt BETWEEN :startDate AND :endDate) OR (t.updatedAt BETWEEN :startDate AND :endDate)) AND t.txStatus = 8")
   List<Transaction> findTransactionsByChannel(@Param("channel") ChannelType channel,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);

   @Query("SELECT t FROM Transaction t WHERE t.txType = 'TTID0002' AND ((t.createdAt BETWEEN :startDate AND :endDate) OR (t.updatedAt BETWEEN :startDate"
   		+ " AND :endDate)) AND t.txStatus = 8 AND t.oppoBankCode = 'CIC0018'")
   List<Transaction> findWalletTransactions(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
   
   @Query("SELECT t FROM Transaction t WHERE (t.txType = 'TTID0002' OR t.txType = 'TTID0001') AND ((t.createdAt BETWEEN :startDate AND :endDate) OR (t.updatedAt BETWEEN :startDate"
	   		+ " AND :endDate)) AND t.txStatus = 8 AND t.oppoBankCode = 'MPESA'")
   List<Transaction> findMpesaTransactions(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
   
   @Query("SELECT t FROM Transaction t WHERE (t.txType = 'TTID0005') AND oppoSubAccount IS NOT NULL AND ((t.createdAt BETWEEN :startDate AND :endDate) OR (t.updatedAt BETWEEN :startDate"
	   		+ " AND :endDate)) AND t.txStatus = 8")
  List<Transaction> findPayBillTransactions(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
   
  @Query("SELECT t FROM Transaction t WHERE (t.txType = 'TTID0005' OR t.txType = 'TTID0006') AND t.oppoSubAccount IS NULL AND ((t.createdAt BETWEEN :startDate AND :endDate) OR (t.updatedAt BETWEEN :startDate"
	   		+ " AND :endDate)) AND t.txStatus = 8")
  List<Transaction> findTillTransactions(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
  
  @Query("SELECT t FROM Transaction t WHERE t.txType = 'TTID0002' AND ((t.createdAt BETWEEN :startDate AND :endDate) OR (t.updatedAt BETWEEN :startDate"
	   		+ " AND :endDate)) AND t.txStatus = 8 AND t.oppoBankCode != 'MPESA' AND t.oppoBankCode != 'CIC0018'")
  List<Transaction> findPesalinkTransactions(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
  
//  t.oppoBankCode != 'MPESA' AND t.oppoBankCode != 'CIC0018'
  
  
  
  
   
   



}
