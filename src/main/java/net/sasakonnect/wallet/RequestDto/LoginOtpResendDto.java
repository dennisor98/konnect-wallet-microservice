package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginOtpResendDto {
  @NotBlank(message="hash is required")
  String hash;
}
