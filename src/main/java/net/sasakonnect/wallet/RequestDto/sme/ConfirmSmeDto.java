package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor()
@NoArgsConstructor()
public class ConfirmSmeDto {
	@NotBlank
	private String otp;

	@NotBlank
	private String smeOnboardingId;

}
