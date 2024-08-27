package net.sasakonnect.wallet.domain;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sasakonnect.wallet.domain.authz.ClientAuthority;

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
	
	@OneToMany(mappedBy = "walletClient")
	private List<WalletClientAccount> walletClientAccount;
	
	
	//base64 icon
	@Column(columnDefinition = "LONGTEXT",nullable=true)
	String appIcon;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "owner_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@OneToMany(mappedBy="client")
	List<ClientAuthority> authorities;
	
	@OneToMany(mappedBy="client")
	List<ClientAppScopes> scopes;
	
	

}
