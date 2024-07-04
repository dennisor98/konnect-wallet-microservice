package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Permission extends BaseWalletDomain implements Serializable {

	@Column(name = "name", unique = true)
	private String name;

	@Column
	private String description;
	@Builder.Default
	@Column(nullable = true, columnDefinition = "boolean default true")
	private Boolean assignable = true;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
