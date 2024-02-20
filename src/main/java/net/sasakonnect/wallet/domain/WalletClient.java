package net.sasakonnect.wallet.domain;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class WalletClient extends BaseWalletDomain {
	@Column(nullable = false)
	String appName;
	@Column(nullable = false)
	String appKey;
	@Column(nullable = false)
	String appSecret;
	@Column(nullable = false)
	String appDescription;
	@Column(nullable = true, columnDefinition = "boolean default false")
	Boolean enabled;
	@Column(nullable = true)
	String callBackUrl;

	@Transient
	private List<WalletClientAccount> walletClientAccounts;
	@ManyToOne
	@JoinColumn(name = "owner_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

}
