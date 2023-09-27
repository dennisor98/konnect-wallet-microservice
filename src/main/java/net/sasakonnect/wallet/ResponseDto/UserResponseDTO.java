package net.sasakonnect.wallet.ResponseDto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
	private String id;
	private Instant createdAt;
	private Instant deletedAt;
	private Instant updatedAt;
	private String firstName;
	private String middleName;
	private String lastName;
	private String address;
	private String gender;
	private int countryCode;
	private String mobile;
	private String idType;
	private String idNumber;
	private String onboardingRequestId;
	private String birthday;
	private String kraPin;
	private String employmentStatus;
	private String monthlyIncome;
	private String refreshToken;
	private String token;

}
