package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {
	List<Wallet> findByUserWalletsUser(User user);

	List<Wallet> findByUserWalletsUser_Id(String userId);

	Optional<Wallet> findByAccountId(String accountId);
	
	

}
