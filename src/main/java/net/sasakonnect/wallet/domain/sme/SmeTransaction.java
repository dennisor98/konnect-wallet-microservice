package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.WalletTransaction;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SmeTransaction extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column
	private String txId;

	@Column
	private String externalTxId;
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
	@Column(nullable=true)
	private String counterpartyName;
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
	
	@Column
	private String remarks;
}
