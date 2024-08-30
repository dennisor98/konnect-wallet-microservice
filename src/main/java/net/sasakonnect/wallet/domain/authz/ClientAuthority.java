package net.sasakonnect.wallet.domain.authz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.WalletClient;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientAuthority extends BaseWalletDomain {
	@ManyToOne
	@JoinColumn(name="authority")
	GlobalAuthority authority;
	
	

	@ManyToOne()
	@JoinColumn(name="client")
	WalletClient client;
	
	@OneToOne
	@JoinColumn(name="creator_id")
	User user;
	
	
	Boolean isOptional;
}
