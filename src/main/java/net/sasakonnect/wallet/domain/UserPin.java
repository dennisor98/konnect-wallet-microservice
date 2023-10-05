package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class UserPin extends BaseWalletDomain implements Serializable {

	@ManyToOne
	private User user;

	@Column(nullable = false)
	private String pin;

	@Column(nullable = false, columnDefinition = "int default 0")
	private int pinAttempts;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}