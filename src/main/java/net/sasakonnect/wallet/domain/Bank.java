package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bank extends BaseWalletDomain implements Serializable {

	public Bank(String bankCode, String bankName, boolean isOperational, boolean isCooperative, String imageurl) {
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.isOperational = isOperational;
		this.imageurl = imageurl;

	}

	@Column(name = "bank_name")
	private String bankName;
	@Column(name = "bank_code")
	private String bankCode;
	@Column
	private boolean isOperational;
	@Column
	private boolean isCooperative;
	@Column
	private String formerName;
	@Column
//  
	@NotNull
	private String imageurl;

	// Constructors, getters, and setters go here

	// Don't forget to add getters and setters for all fields, including 'id'.
}
