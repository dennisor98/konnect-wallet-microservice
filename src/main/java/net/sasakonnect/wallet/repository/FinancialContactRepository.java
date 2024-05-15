package net.sasakonnect.wallet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.FinancialContact;

public interface FinancialContactRepository extends JpaRepository<FinancialContact,String>{
   Page<FinancialContact> findByAccountId(String accountId,Pageable page);
   
   Optional<FinancialContact> findByoppoAccountId(String oppoAccountId);
   
   Optional<FinancialContact> findByAccountIdAndOppoAccountId(String accountId, String oppoAccountId);
   
   Page<FinancialContact> findByAccountIdAndTxType(String accountId,String txType,Pageable pageable);

}
