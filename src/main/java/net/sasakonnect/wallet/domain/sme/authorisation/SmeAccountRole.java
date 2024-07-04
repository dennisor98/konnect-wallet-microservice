package net.sasakonnect.wallet.domain.sme.authorisation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.sme.SmeAccount;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeAccountRole extends BaseWalletDomain {
	@OneToOne
	@JoinColumn(name="sme_account_id")
	SmeAccount smeAccount;
	
   @Column(nullable=false)
   String name;
   
   @Column(nullable=false)
   String description;
   
}
