package net.sasakonnect.wallet.domain.sme.authorisation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeAccountPermissions extends BaseWalletDomain {
	@Column(nullable=false)
	String name;

	@Column(nullable=false)
	String description;

	@Column(nullable=false)
	String category;
}
