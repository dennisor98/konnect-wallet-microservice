package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Wallet extends BaseWalletDomain implements Serializable {

	@Column
	private String accountId;

	@Column
	private String shortcode;

	@Column
	private String accountType;

	@OneToMany(mappedBy = "wallet")
	private List<WalletTransaction> walletTransactions;

	@OneToMany(mappedBy = "wallet")
	private List<UserWallet> userWallets;

	@OneToMany(mappedBy = "wallet")
	private List<WalletFreeze> walletFreezes;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}