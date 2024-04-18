package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class WalletTransaction extends BaseWalletDomain implements Serializable {
	@ManyToOne
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;
}
