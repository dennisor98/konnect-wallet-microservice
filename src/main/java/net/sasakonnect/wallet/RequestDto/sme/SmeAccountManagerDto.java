package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SmeAccountManagerDto {

  @NotNull(message="userId is required")
  String userId;
  
  @NotNull(message="accountId is required")
  String accountId;
}
