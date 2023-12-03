package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role", indexes = {
		@Index(name = "user_role_unique_index", columnList = "user_id, role_id", unique = true) })
@Data()
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends BaseWalletDomain implements Serializable {

	@Column(name = "user_id")
	private String userId;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private User user;

	@Column(name = "role_id")
	private String roleId;

	@ManyToOne
	@JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Role role;

	@Column(name = "is_deactivated", columnDefinition = "bool default false")
	private boolean isDeactivated;

	@ManyToOne
	@JoinColumn(name = "assigner_id", referencedColumnName = "id")
	private User assigner;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}