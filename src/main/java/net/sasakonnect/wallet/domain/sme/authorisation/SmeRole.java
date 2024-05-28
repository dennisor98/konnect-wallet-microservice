package net.sasakonnect.wallet.domain.sme.authorisation;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Enterprise;

@Entity
@Data
public class SmeRole extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -6448849838407985298L;
	@ManyToOne
	@JoinColumn(name = "enterprise_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Enterprise enterprise;

	@ManyToOne
	@JoinColumn(name = "creator_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@OneToMany(mappedBy = "sme_role", cascade = CascadeType.ALL)
	private List<SmeUserRole> smeUserRoles;

	@Column(name = "role_name", unique = true)
	private String roleName;

	@Column
	private String description;
}
