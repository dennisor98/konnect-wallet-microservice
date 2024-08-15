package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SmeMultiAccountDto {
  @NotNull(message="smeId is required")
  String smeId;
  
  @NotNull(message="smeId is required")
  String accountName;
}
