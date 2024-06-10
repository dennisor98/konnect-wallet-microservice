package net.sasakonnect.wallet.domain.sme;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.enums.sme.OperatingMode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SmeBusinessAccount extends BaseWalletDomain{
   @OneToOne()
   @JoinColumn(name="smeId")
   Sme smeId;
   
   
   @Column
   OperatingMode operatingMode;
}
