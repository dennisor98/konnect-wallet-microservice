package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class UserLoggingAttempts extends BaseWalletDomain implements Serializable {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "device_id")
	private Device device;

}
