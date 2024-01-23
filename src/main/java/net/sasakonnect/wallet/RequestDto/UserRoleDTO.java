package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO {
  @NotNull(message="Role id cannot be null")
  String roleId;
  
  
  @NotNull(message="User id cannot be null")
  String userId;
  
}
