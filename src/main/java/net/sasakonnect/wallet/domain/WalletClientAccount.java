package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletClientAccount extends BaseWalletDomain {
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private FinancialInstituation accountType;

	@Column(nullable = true)
	private String paybillNumber;

	@Column(nullable = true)
	private String tillNumber;

	@Column(nullable = true)
	private String payBillAccountNo;

	@Column(nullable = true)
	private String walletAccountNo;
	@Column(nullable = true)
	private String bankCode;
	@ManyToOne
	@JoinColumn(name = "wallet_client_id", referencedColumnName = "id")
	private WalletClient walletClient;

}
