package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import net.sasakonnect.wallet.domain.sme.SmeTransaction;


public interface SmeTransactionRepository extends JpaRepository<SmeTransaction,String>{
  Optional<SmeTransaction> findByTxId(String txId);
}
