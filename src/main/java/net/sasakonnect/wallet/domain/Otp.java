package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.time.Instant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@ToString
@Slf4j
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
	@OnDelete(action = OnDeleteAction.CASCADE)
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
		log.debug("current time in utc" + utcNow.getEpochSecond());

		// Convert createdAt to an Instant (assuming createdAt is in milliseconds since
		// epoch)
		Instant createdAtInstant = Instant.ofEpochMilli(this.getCreatedAt().getTime());
		log.debug("time created" + utcNow.getEpochSecond());

		// Add ttl seconds to createdAt
		Instant expirationInstant = createdAtInstant.plusSeconds(this.getTtl());
		log.debug("time after adding  ttl " + expirationInstant.getEpochSecond());
		log.debug("expiry is after current time ? " + expirationInstant.isAfter(utcNow));

		// Check if the expiration time is before the current local time
		if (expirationInstant.isAfter(utcNow)) {
			log.warn("otp is valid");
			log.debug("expiry is after current time ? " + expirationInstant.isAfter(utcNow));

			return true;
		}
		log.warn("otp is not valid ");

		return false;

	}
	// Other constructors, getters, and setters
}
