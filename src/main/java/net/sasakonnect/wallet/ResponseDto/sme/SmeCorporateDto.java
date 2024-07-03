package net.sasakonnect.wallet.ResponseDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmeCorporateDto {
  @NotBlank(message="userId cannot be empty")
  String userId;
}
