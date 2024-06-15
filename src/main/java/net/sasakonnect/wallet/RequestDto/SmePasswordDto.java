package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmePasswordDto {
  @NotBlank
  String member_id;
}
