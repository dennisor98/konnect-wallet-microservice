package net.sasakonnect.wallet.ResponseDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeRoleDto {
  @NotBlank(message="name cannot be empty")
  String name;
  
  @NotBlank(message="description cannot be empty")
  String description;

}
