package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString

public class Otp extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = true)
	private String hash;

	@Column(nullable = false)
	private String code;

	@Column(columnDefinition = "int default 120")
	private int ttl;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	// Constructors, getters, and setters

	public Otp() {
	}

	public Otp(String phoneNumber, String hash, String code, int ttl, User user) {
		this.phoneNumber = phoneNumber;
		this.hash = hash;
		this.code = code;
		this.ttl = ttl;
		this.user = user;
	}

	public boolean isValid() {
		if (this.getDeletedAt() != null) {
			return false;
		}
		Instant utcNow = Instant.now();

		// Convert createdAt to an Instant (assuming createdAt is in milliseconds since
		// epoch)
		Instant createdAtInstant = Instant.ofEpochMilli(this.getCreatedAt().getTime());

		// Add ttl seconds to createdAt
		Instant expirationInstant = createdAtInstant.plusSeconds(this.getTtl());

		// Check if the expiration time is before the current local time
		if (expirationInstant.isAfter(utcNow)) {
			return true;
		}
		return false;

	}
	// Other constructors, getters, and setters
}
