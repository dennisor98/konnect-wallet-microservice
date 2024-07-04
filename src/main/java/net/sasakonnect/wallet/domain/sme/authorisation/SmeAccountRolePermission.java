package net.sasakonnect.wallet.domain.sme.authorisation;

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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeAccountRolePermission extends BaseWalletDomain {
	private static final long serialVersionUID = -6448849838407985298L;
	@ManyToOne
	@JoinColumn(name = "sme_account_role", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmeAccountRole role;
	
	@ManyToOne
	@JoinColumn(name = "creator_user_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User creator;

	@ManyToOne
	@JoinColumn(name = "sme_account_permission_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private SmeAccountPermissions permission;
}
