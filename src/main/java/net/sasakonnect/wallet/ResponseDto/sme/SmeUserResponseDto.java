package net.sasakonnect.wallet.ResponseDto.sme;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.ResponseDto.UserResponseDTO;
import net.sasakonnect.wallet.domain.Wallet;
@Data
@Builder
public class SmeUserResponseDto {
	private String id;
	private Date createdAt;
	private Date deletedAt;
	private Date updatedAt;
	private String firstName;
	private String middleName;
	private String lastName;
	private String refreshToken;
	private String token;
	private String open_id;
	private Object profileImage;
	private String mobile;
	private String sme;
	private int countryCode;
}
