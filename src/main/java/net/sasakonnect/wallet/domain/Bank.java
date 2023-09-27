package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Bank extends BaseWalletDomain implements Serializable {

	@Column(name = "bank_name")
	private String bankName;

	@Column(name = "bank_code")
	private String bankCode;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
