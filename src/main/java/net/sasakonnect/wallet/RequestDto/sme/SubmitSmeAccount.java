package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitSmeAccount {
	@NotBlank(message = "Submit account sme")
	private String onboardingRequestId;
}
