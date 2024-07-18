package net.sasakonnect.wallet.repository.sme;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.sme.SmeTransaction;


public interface SmeTransactionRepository extends JpaRepository<SmeTransaction,String>{
  Optional<SmeTransaction> findByTxId(String txId);
  
  Page<SmeTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
  
  @Query("SELECT t FROM SmeTransaction t WHERE t.accountId =:accountId AND t.amount > 0 ORDER BY t.createdAt DESC")
  List<SmeTransaction> findAllInByAccountId(@Param("accountId") String accountId);
  
  @Query("SELECT t FROM SmeTransaction t WHERE t.accountId =:accountId AND t.amount < 0 ORDER BY t.createdAt DESC")
  List<SmeTransaction> findAlloutByAccountId(@Param("accountId") String accountId);
  
  List<SmeTransaction> findAllByAccountIdOrderByCreatedAtDesc(@Param("accountId") String accountId);
}
