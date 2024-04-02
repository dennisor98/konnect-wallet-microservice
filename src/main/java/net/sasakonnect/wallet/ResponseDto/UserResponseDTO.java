package net.sasakonnect.wallet.ResponseDto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.domain.ProfileImage;
import net.sasakonnect.wallet.domain.Wallet;

@Data
@Builder
public class UserResponseDTO {
	private String id;
	private Date createdAt;
	private Date deletedAt;
	private Date updatedAt;
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
	private String open_id;
	private Object profileImage;
	private List<Wallet> wallets;

}
