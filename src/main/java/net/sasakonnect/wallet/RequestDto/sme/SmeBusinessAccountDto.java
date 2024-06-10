package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmeBusinessAccountDto {
	@NotBlank(message="sme_id cannot be null")
    String sme_id;
	
	@NotBlank(message="operatingmode cannot be null")
	String Operatingmode;
}
