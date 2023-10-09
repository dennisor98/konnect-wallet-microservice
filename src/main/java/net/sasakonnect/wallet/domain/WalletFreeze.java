package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class WalletFreeze extends BaseWalletDomain implements Serializable {

	@ManyToOne
	private Wallet wallet; // Change this field to many-to-one

	@Column(nullable = false)
	@ColumnDefault("true")
	private boolean isActive;

	@ManyToOne
	@JoinColumn(name = "enforced_by_user_id")
	private User enforcedByUser;

	@Column(nullable = false)
	private String reason;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateEnforced;
}
