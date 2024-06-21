package net.sasakonnect.wallet.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.EmploymentStatus;
import net.sasakonnect.wallet.enums.Gender;
import net.sasakonnect.wallet.enums.IdType;
import net.sasakonnect.wallet.enums.MonthlyIncome;

@Entity()
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectedAccount extends BaseWalletDomain {
	@Column
	private String firstName;
	
	@Column
	private String middleName;

	@Column(nullable = false, length = 50)
	private String lastName;

	@Column(nullable = true, length = 50)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false)
	private int countryCode;

	@Column(nullable = false, unique = false, length = 50)
	private String mobile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IdType idType;

	@Column(nullable = false, unique = false, length = 50)
	private String idNumber;
   
	@Column(length = 50, unique = false)
	private String onboardingRequestId;

	@Column(nullable = false)
	private Date birthday;

	@Column(nullable = true, length = 50)
	private String kraPin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private EmploymentStatus employmentStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private MonthlyIncome monthlyIncome;
	
	@Column(nullable = true)
	private String status;
	
	@Column(nullable=true)
	Date dateCreated;
	
	@Column(nullable=true)
	private String rejectionReason;
}
