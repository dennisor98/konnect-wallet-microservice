package net.sasakonnect.wallet.RequestDto.authz;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetClientAuthsDto {
  @NotNull(message="appKey is missing")
  String appKey;
  
  @NotNull(message="appSecret is missing")
  String appSecret;
}
