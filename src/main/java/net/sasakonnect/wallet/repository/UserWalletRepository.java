package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.UserWallet;
import net.sasakonnect.wallet.domain.UserWalletId;

public interface UserWalletRepository extends JpaRepository<UserWallet, UserWalletId> {

}
