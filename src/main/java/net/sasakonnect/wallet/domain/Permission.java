package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Permission extends BaseWalletDomain implements Serializable {

	@Column
	private String name;

	@Column
	private String description;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
