package net.sasakonnect.wallet.domain.sme.authorisation;

import java.io.Serializable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmeUserRole extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 5208946861201323533L;
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmeCorporate user;

	@ManyToOne
	@JoinColumn(name = "sme_account_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Sme smeAccount;

	@ManyToOne
	@JoinColumn(name = "sme_role_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmeRole smeRole;

}
