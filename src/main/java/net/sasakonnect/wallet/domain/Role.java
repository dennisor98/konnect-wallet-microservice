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
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseWalletDomain implements Serializable {

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