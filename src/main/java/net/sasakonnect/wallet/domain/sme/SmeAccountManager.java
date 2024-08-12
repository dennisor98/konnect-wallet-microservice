package net.sasakonnect.wallet.domain.sme;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class SmeAccountManager extends BaseWalletDomain{
   @ManyToOne()
   @JoinColumn(name="sme_account")
   SmeAccount smeAccount;
   
   @OneToOne()
   @JoinColumn(name="wallet_user_id")
   User user;
   
   @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
   private Boolean verified;
}
