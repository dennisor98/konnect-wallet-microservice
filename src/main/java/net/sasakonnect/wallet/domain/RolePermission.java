package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission extends BaseWalletDomain implements Serializable {

	@ManyToOne()
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne()
	@JoinColumn(name = "permission_id")
	private Permission permission;

	@ManyToOne()
	@JoinColumn(name = "creator_id", referencedColumnName = "id")
	private User user;

}
