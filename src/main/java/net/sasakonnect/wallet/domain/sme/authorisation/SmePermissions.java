package net.sasakonnect.wallet.domain.sme.authorisation;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class SmePermissions extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -6448849838407985298L;
	@Column(name = "name", unique = true)
	private String name;

	@Column
	private String description;
	
	@Column
	private String category;

}
