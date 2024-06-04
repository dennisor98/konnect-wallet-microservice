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
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmeAccount extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "accountNo", nullable = true)
	private String accountNo;

	@ManyToOne
	@JoinColumn(name = "sme_id")
	private Sme sme;
}
