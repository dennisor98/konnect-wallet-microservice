package net.sasakonnect.wallet.RequestDto.authz;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetClientAuthsDto {
  @NotNull(message="appKey is missing")
  String appKey;
  
  @NotNull(message="appSecret is missing")
  String appSecret;
}
