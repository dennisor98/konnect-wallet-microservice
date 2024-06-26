package net.sasakonnect.wallet.domain.sme;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SmeCorporate extends BaseWalletDomain{
  @OneToOne()
  @JoinColumn(name="user_id")
  User user;
  
  @OneToOne()
  @JoinColumn(name="role_id")
  SmeRole role;
  
}
