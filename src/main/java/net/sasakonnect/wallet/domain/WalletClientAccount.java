package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
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
public class WalletClientAccount extends BaseWalletDomain {
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private FinancialInstituation accountType;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isPrimary;

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
	@Column(nullable = true)
	private String bankAccount;

	@ManyToOne()
	WalletClient walletClient;

}
