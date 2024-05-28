package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmeAccount extends BaseWalletDomain implements Serializable {

	@ManyToOne
	@JoinColumn(name = "enterprise_id")
	private Enterprise enterprise;

	private static final long serialVersionUID = -2020925333315146974L;
	@Column(name = "country_code", nullable = false)
	private String countryCode;

	@Column(name = "status", nullable = true)
	private Integer status;

	@Column(name = "completeTime", nullable = true)
	private Long completeTime;

	@Column(name = "accountNo", nullable = true)
	private String accountNo;

	@Column(name = "business_type", nullable = false)
	private String businessType;

	@Column(name = "mobile", nullable = false)
	private String mobile;

	@Column(name = "email")
	private String email;

	@Column(name = "otp_type", nullable = false)
	private String otpType;

	@Column(name = "onboardingRequestId", nullable = true)
	private String onboardingRequestId;
}
