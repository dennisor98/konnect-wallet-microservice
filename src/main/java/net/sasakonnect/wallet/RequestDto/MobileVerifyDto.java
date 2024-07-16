package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MobileVerifyDto {
  String countryCode;
  
  @NotBlank(message="mobile is required")
  String mobileNumber;
}
