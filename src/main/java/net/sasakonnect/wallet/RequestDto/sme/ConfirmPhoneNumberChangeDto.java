package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmPhoneNumberChangeDto {
	@NotEmpty(message = "application id or transaction id required")
	String operationId;
	@NotEmpty(message = "otp required")

	String otp;

}
