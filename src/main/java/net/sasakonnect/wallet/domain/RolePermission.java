package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RolePermission extends BaseWalletDomain implements Serializable {

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne
	@JoinColumn(name = "permission_id")
	private Permission permission;

	@ManyToOne
	@JoinColumn(name = "creator_id", referencedColumnName = "id")
	private User user;

}
