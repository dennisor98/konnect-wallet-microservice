package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
@IdClass(UserWalletId.class)
public class UserWallet extends BaseWalletDomain implements Serializable {

	@ManyToOne(fetch = FetchType.LAZY) // Add fetch = FetchType.LAZY
	@JoinColumn(name = "wallet_id")
    @JsonIgnore()
	private Wallet wallet;

	@ManyToOne(fetch = FetchType.LAZY) // Add fetch = FetchType.LAZY
	@JoinColumn(name = "user_id")
	 @JsonIgnore()
	private User user;

	@Override
	public String toString() {
		return "UserWallet{" + "id=" + getId() + ", wallet=" + (wallet != null ? wallet.getId() : "null") + ", user="
				+ (user != null ? user.getId() : "null") + '}';
	}
}
