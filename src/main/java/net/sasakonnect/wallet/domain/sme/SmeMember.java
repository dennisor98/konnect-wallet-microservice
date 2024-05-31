package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;

@Entity

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmeMember extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 516226619396771493L;
	@Column(name = "member_id", nullable = false)
	private String member_id;

	@ManyToOne
	@JoinColumn(name = "sme_account_id")
	private SmeAccount sme_account;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
