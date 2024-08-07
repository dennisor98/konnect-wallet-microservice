package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SmeCorporate extends BaseWalletDomain implements Serializable{
	private static final long serialVersionUID = -4463864574321745687L;
  @OneToOne()
  @JoinColumn(name="user_id",unique=false)
  User user;
  
  @OneToOne()
  @JoinColumn(name="role_id",unique=false)
  SmeRole role;
  
  @ManyToOne
  @JoinColumn(name="sme_id")
  private Sme smes;
  
}
