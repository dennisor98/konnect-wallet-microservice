package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}
