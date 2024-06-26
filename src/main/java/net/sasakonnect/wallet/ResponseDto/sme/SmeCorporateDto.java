package net.sasakonnect.wallet.ResponseDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmeCorporateDto {
  @NotBlank()
  String userId;
}
