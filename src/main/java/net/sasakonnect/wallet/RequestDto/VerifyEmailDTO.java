package net.sasakonnect.wallet.RequestDto;

import com.google.auto.value.AutoValue.Builder;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Builder
@Data
public class VerifyEmailDTO {
	@NotBlank(message = "email cannot be empty")
	public String email;

	@NotBlank(message = "OTP cannot be empoty")
	public String OTP;

	@NotBlank(message = "Verification hash required")
	public String verificationHash;

}
