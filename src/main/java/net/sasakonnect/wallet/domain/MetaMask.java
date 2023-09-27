package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "meta_mask")
@Data
public class MetaMask extends BaseWalletDomain {

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "flow_id")
	private String flowId;
}