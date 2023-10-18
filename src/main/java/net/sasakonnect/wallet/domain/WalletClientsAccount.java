package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletClientsAccount extends BaseWalletDomain {
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
	@OneToOne(mappedBy = "walletClientsAccount")
	private WalletClient walletClient;

}
