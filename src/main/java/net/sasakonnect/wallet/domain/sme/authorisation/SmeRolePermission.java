package net.sasakonnect.wallet.domain.sme.authorisation;

import java.io.Serializable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity

@Builder
public class SmeRolePermission extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -6448849838407985298L;
	@ManyToOne
	@JoinColumn(name = "sme_user_role", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmeUserRole smeUserRole;

	@ManyToOne
	@JoinColumn(name = "sme_permission_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmePermissions smePermissions;
}
