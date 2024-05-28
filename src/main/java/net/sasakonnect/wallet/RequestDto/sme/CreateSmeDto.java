package net.sasakonnect.wallet.RequestDto.sme;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.sme.BusinessType;

@Data
@Builder
@AllArgsConstructor()
@NoArgsConstructor()
public class CreateSmeDto {
	@NotBlank
	private String countryCode;

	@NotNull
	@Schema(description = "Type of business", allowableValues = { "SOLE_PROPRIETORSHIP", "LIMITED_LIABILITY_COMPANY",
			"PARTNERSHIP" })
	private BusinessType businessType;

	@NotBlank
	private String mobile;

	private String email;

	@NotBlank
	private String otpType;
	@NotBlank
	private String enterprise_id;
}
