package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.RejectedAccount;

public interface RejectedAccountRepository extends JpaRepository<RejectedAccount,String> {
   void deleteByIdNumber(String idNumber);
}
