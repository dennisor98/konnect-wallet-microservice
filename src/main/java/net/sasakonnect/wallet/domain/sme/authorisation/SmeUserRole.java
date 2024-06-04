package net.sasakonnect.wallet.domain.sme.authorisation;

import java.io.Serializable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;

@Entity

@Builder
public class SmeUserRole extends BaseWalletDomain implements Serializable {
	private static final long serialVersionUID = 5208946861201323533L;
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@ManyToOne
	@JoinColumn(name = "sme_account_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Sme smeAccount;

	@ManyToOne
	@JoinColumn(name = "sme_role_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmeRole sme_role;

}
