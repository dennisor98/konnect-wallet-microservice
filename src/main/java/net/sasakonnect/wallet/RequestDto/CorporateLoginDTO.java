package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorporateLoginDTO {
  @NotNull(message="email cannot be empty")
  @Email(message="email must be valid")
   String phone;
  
}
