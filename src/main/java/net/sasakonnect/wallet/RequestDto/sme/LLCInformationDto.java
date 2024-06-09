package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.sme.BusinessIndustry;
import net.sasakonnect.wallet.enums.sme.OperatingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LLCInformationDto {
	@NotBlank()
	@NotNull()
	private String onboardingRequestId;
	
	@NotBlank()
	@NotNull()
	private String businessName;
	
	@NotBlank()
	@NotNull()
	private String businessCerNum;
	
	@NotBlank()
	@NotNull()
	private String kraPin;
	
	@NotBlank()
	@NotNull()
	private OperatingMode operatingMode;
	
	private String specifyMode;
	
	@NotBlank()
	@NotNull()
	private BusinessIndustry businessIndustry;
	
	
	private String specifyIndustry;
	
	@NotBlank()
	@NotNull()
	private String businessAddress;
}
