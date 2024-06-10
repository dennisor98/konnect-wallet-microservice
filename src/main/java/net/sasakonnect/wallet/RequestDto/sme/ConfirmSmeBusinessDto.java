package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmSmeBusinessDto {
  @NotBlank(message="applicationId cannot be empty")
  String applicationId;
  
  @NotBlank(message="applicationId cannot be empty")
  String smsCode;
}
