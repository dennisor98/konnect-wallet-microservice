package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChangeUserPhoneNumberDto {
	@NotEmpty(message = "user account required")

	String userAccount;
	@NotEmpty(message = "the new phone number required")

	String newPhoneNumber;

}
