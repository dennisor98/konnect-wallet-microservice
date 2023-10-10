package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency", uniqueConstraints = { @UniqueConstraint(columnNames = { "country", "currencyCode" }) })
public class Currency extends BaseWalletDomain implements Serializable {
	@Column()
	private String country;
	@Column()
	private String currencyName;
	@Column()
	private String currencyCode;
	@Column()
	private int decimalPlaces;
	@Builder.Default
	@Column()
	private boolean isEnabled = true;

	public Currency(String country, String currencyName, String currencyCode, int decimal) {
		this.country = country;
		this.currencyCode = currencyCode;
		this.currencyName = currencyName;
		this.decimalPlaces = decimal;
	}

	// Constructors, getters, setters, and other methods can be added as needed.
}
