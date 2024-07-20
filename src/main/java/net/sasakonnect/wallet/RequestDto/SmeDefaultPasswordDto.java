package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeDefaultPasswordDto {
  @NotBlank
  String userId;
}
