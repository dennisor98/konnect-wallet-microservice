package net.sasakonnect.wallet.domain.sme.authorisation;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity

@Builder
public class SmePermissions extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -6448849838407985298L;
	@Column(name = "name", unique = true)
	private String name;

	@Column
	private String description;

}
