package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeWindowPinDto {
	@NotBlank(message="password is required")
  String password;
}
