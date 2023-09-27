package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_notified")
public class UserNotified extends BaseWalletDomain implements Serializable {

	@ManyToOne
	@JoinColumn(name = "notification_id")
	private Notification notification;

	@Column(nullable = false)
	private String message_id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private Boolean isSent;

	@Column
	private String comment;

	@Column(nullable = false)
	private Boolean isRead;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
