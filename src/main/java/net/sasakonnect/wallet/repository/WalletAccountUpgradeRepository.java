package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.WalletAccountUpgrade;

public interface WalletAccountUpgradeRepository extends JpaRepository<WalletAccountUpgrade, String> {

}
