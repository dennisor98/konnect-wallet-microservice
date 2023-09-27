package net.sasakonnect.wallet.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Notification extends BaseWalletDomain {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User sender;

	@Column(nullable = true, length = 1000)
	private String message;

	@Column(nullable = true)
	private String title;

	@ManyToMany
	@JoinTable(name = "user_notified", joinColumns = @JoinColumn(name = "notification_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> user_notified;
}
