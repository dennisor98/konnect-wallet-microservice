package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.enums.sme.BusinessIndustry;
import net.sasakonnect.wallet.enums.sme.OperatingMode;
import net.sasakonnect.wallet.serde.BusinessIndustryConverter;
import net.sasakonnect.wallet.serde.OperatingModeConverter;

@Entity
@Data
@Builder
public class SmeAccountDetails extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -557798343708381347L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sme_account_id", nullable = false)
	private SmeAccount account;

	@Column(nullable = false)
	private String businessName;

	@Column(nullable = false)
	private String businessCerNum;

//	@Column(nullable = false)
//	private Integer operatingMode;

	@Convert(converter = OperatingModeConverter.class)
	@Column(nullable = false)
	private OperatingMode operatingMode;

	@Column(nullable = true)
	private String specifyMode;

	@Column(nullable = true)
	private String firstName;

	@Column(nullable = true)
	private String lastName;

	@Column(nullable = true)
	private String middleName;

	@Column(nullable = true)
	private Integer gender;

	@Column(nullable = true)
	private LocalDate birthday;

	@Column(nullable = true)
	private String idNumber;

	@Column(nullable = false)
	private String kraPin;

	@Column(nullable = true)
	private String kinFullName;

	@Column(nullable = true)
	private String kinRelationship;

	@Column(nullable = true)
	private String kinCountryCode;

	@Column(nullable = true)
	private String kinMobile;
	@Convert(converter = BusinessIndustryConverter.class)
	@Column(nullable = false)
	private BusinessIndustry businessIndustry;

	@Column(nullable = true)
	private String specifyIndustry;

	@Column(nullable = false)
	private String businessAddress;

}
