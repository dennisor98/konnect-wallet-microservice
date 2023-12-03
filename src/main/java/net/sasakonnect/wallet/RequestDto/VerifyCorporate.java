package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCorporate {
  @NotNull(message="corporateId cannot be null")
  String corporateId;
  
  @NotNull(message="isActive cannot be empty")
  Boolean isActive;
  
  @NotNull(message="isActive cannot be empty")
  Boolean isVerified;
  
  
}
