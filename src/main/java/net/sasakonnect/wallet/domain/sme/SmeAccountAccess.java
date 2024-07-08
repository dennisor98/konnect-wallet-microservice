package net.sasakonnect.wallet.domain.sme;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class SmeAccountAccess extends BaseWalletDomain{
   @ManyToOne()
   @JoinColumn(name="sme_corporate_id")
   SmeCorporate sme;
   
   @OneToOne()
   @JoinColumn(name="sme_account_id")
   SmeAccount smeAccount;
}
