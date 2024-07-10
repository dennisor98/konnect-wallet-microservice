package net.sasakonnect.wallet.domain.sme.authorisation;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SmeAccountUserRole extends BaseWalletDomain{
   @ManyToOne
   @JoinColumn(name="sme_corporate_id")
   SmeCorporate smeCorporate;
   
   @ManyToOne
   @JoinColumn(name="role_id")
   SmeAccountRole role;
   
   @ManyToOne
   @JoinColumn(name="creator_user_id")
   User user;
}
