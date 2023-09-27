package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class UserWallet extends BaseWalletDomain implements Serializable {

	@ManyToOne
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
