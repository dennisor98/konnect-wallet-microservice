package net.sasakonnect.wallet.RequestDto.sme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.sme.BusinessIndustry;
import net.sasakonnect.wallet.enums.sme.OperatingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LLCInformationDto {
	private String onboardingRequestId;
	private String businessName;
	private String businessCerNum;
	private String kraPin;
	private OperatingMode operatingMode;
	private String specifyMode;
	private BusinessIndustry businessIndustry;
	private String specifyIndustry;
	private String businessAddress;
}
