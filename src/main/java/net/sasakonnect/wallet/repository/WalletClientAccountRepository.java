package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.WalletClientAccount;

public interface WalletClientAccountRepository extends JpaRepository<WalletClientAccount, String> {

}
