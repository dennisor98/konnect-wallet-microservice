package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirebaseToken extends BaseWalletDomain implements Serializable {

	@Column(nullable = false, unique = false, columnDefinition = "TEXT")
	private String token;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true)
	@OnDelete(action = OnDeleteAction.SET_NULL) // Configure to set the user field to null on user deletion
	private User user;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
