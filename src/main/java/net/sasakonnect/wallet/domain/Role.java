package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Role extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -8087130723469587015L;

	public Role() {

	}

	@Column(name = "role_name", unique = true)
	private String roleName;

	@Column
	private String description;

	@ManyToOne
	@JoinColumn(name = "creator_id", referencedColumnName = "id")
	private User user;

	@OneToMany(mappedBy = "role")
	private List<RolePermission> rolePermissions;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}