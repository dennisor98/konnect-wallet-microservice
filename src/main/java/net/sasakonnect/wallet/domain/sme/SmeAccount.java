package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SmeAccount extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "accountNo", nullable = true)
	private String accountNo;
	@Column(name = "appliactionId", nullable = true)
	private String appliactionId;

	@Column(name = "accountName", nullable = true)
	private String accountName;

	@ManyToOne
	@JoinColumn(name = "sme_id")
	private Sme sme;
}
