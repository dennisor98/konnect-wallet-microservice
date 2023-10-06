package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {

}
