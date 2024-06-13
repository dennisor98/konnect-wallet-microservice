package net.sasakonnect.wallet.domain.sme;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmePassword extends BaseWalletDomain{
   @Column()
   String password;
   
   @OneToOne()
   @JoinColumn(name="member_id")
   SmeMember member_id;
   
   @Column(columnDefinition="BOOLEAN DEFAULT false")
   private Boolean isDefault;

}
