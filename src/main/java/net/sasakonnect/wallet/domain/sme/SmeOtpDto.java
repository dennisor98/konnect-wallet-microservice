package net.sasakonnect.wallet.domain.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeOtpDto {
  @NotBlank(message="otp cannot be empty")
  String otp;
  
  @NotBlank(message="hash cannot be empty")
  String hash;
}
