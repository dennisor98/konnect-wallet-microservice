package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column
	private String txId;

	@Column
	private String externalTxId;

	@OneToMany(mappedBy = "transaction")
	private List<WalletTransaction> walletTransactions;

	@Column
	private String accountId;

	@Column
	private String accountName;

	@Column
	private String txType;

	@Column
	private String oppoBankCode;

	@Column
	private String oppoAccountId;

	@Column
	private String oppoSubAccount;

	@Column
	private Integer txStatus;

	@Column
	private String mpesaBusinessPayType;

	@Column
	private String oppoAccountName;

	@Column
	private String extInfo;
	@Column
	private String oppoChannelId;

	@Column
	private String thirdPartyTxType;

	@Column
	private String currency;

	@Column(precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(precision = 10, scale = 2)
	private BigDecimal feeAmount;

	@Column(precision = 10, scale = 2)
	private BigDecimal balance;

	@Column
	private Long completeTime;

	@Column
	private String notificationType;
	@Column
	private String requestId;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
