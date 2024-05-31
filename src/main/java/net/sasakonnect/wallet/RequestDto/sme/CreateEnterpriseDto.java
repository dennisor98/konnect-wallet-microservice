package net.sasakonnect.wallet.RequestDto.sme;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEnterpriseDto {
	@NotBlank
	@Schema(description = "Name of the enterprise")
	private String name;

	@NotBlank
	@Schema(description = "Industry sector the enterprise operates in")
	private String industry;

	@NotBlank
	@Schema(description = "Ownership type of the enterprise")
	private String ownership;

	@Schema(description = "Mission statement of the enterprise")
	private String mission;

	@Schema(description = "Vision statement of the enterprise")
	private String vision;

}
