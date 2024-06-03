package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity

@Builder
public class SmeDocuments extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 9213619287786747761L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sme_account_id", nullable = false)
	private Sme account;

	@Column(nullable = false)
	private String mediaBase64;

	@Column(nullable = false)
	private Integer mediaType;

	@Column(nullable = true)
	private String contentType;
}
