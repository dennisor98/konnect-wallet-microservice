package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor

public class UserJob extends BaseWalletDomain implements Serializable {

	@Column(nullable = true)
	private String jobId;
	@ManyToOne
	@JoinColumn(name = "user_id") // Name of the foreign key column in UserJob table
	private User user;

	@Column(nullable = true, length = 2048) // Increased length for downloadLink field
	private String downloadLink;
	@Column() // Example of a nullable field
	private Boolean isComplete;

	public UserJob() {
		this.isComplete = false; // Set default value
	}

}
