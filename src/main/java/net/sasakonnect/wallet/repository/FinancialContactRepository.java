package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.FinancialContact;

public interface FinancialContactRepository extends JpaRepository<FinancialContact,String>{
  
   Page<FinancialContact> findByAccountId(String accountId,Pageable page);
   
   Optional<FinancialContact> findByoppoAccountId(String oppoAccountId);
   
   Optional<FinancialContact> findByAccountIdAndOppoAccountIdAndTxType(String accountId, String oppoAccountId,String txType);
   
   
   @Query("SELECT f FROM FinancialContact f WHERE f.accountId =:accountId AND (f.txType = 'TTID0002'"
   		+ "OR f.txType = 'TTID0001') AND (f.oppoChannelId = 'M-PESA' OR f.oppoBankCode = 'M-PESA')")
   Page<FinancialContact> findMpesaTransactionContacts(@Param("accountId") String accountId,Pageable pageable);
   
   @Query("SELECT f FROM FinancialContact f WHERE f.accountId =:accountId AND f.txType = 'TTID0005'"
	   		+"AND (f.oppoChannelId = 'M-PESA' OR f.oppoBankCode = 'M-PESA') AND f.oppoSubAccountId IS NULL")
   Page<FinancialContact> findTillContacts(@Param("accountId") String accountId,Pageable pageable);
   
   
   @Query("SELECT f FROM FinancialContact f WHERE f.accountId =:accountId AND f.txType = 'TTID0005'"
	   		+"AND (f.oppoChannelId = 'M-PESA' OR f.oppoBankCode = 'M-PESA') AND f.oppoSubAccountId IS NOT NULL")
  Page<FinancialContact> findPaybillContacts(@Param("accountId") String accountId,Pageable pageable);
   
   
   @Query("SELECT f FROM FinancialContact f WHERE f.accountId =:accountId AND f.txType = 'TTID0002'"
	   		+"AND (f.oppoBankCode = 'CIC0018' OR f.oppoChannelId = 'CIC0018')")
 Page<FinancialContact> findWalletContacts(@Param("accountId") String accountId,Pageable pageable);
   
   @Query("SELECT f FROM FinancialContact f WHERE f.accountId =:accountId AND f.txType = 'TTID0002'"
	   		+"AND f.oppoBankCode != 'CIC0018' AND f.oppoChannelId != 'CIC0018'")
Page<FinancialContact> findPesaLinkContacts(@Param("accountId") String accountId,Pageable pageable);

   
   @Query("SELECT f FROM FinancialContact f WHERE f.accountId =:accountId AND f.txType = 'TTID0006'")
Page<FinancialContact> findUtilityContacts(@Param("accountId") String accountId,Pageable pageable);
   
 @Query("SELECT f FROM FinancialContact f WHERE (f.oppoAccountId LIKE %:queryString% OR f.oppoAccountName LIKE %:queryString%) AND f.txType = : txType")
 Page<FinancialContact> searchContact(@Param("queryString") String searchTerm,Pageable pageable);

   
 
}
