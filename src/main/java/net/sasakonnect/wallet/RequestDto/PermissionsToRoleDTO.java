package net.sasakonnect.wallet.RequestDto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionsToRoleDTO {
  @NotNull(message="Role ID is required")
  String roleId;
  
  @NotNull(message="Permisssion ids cannot be null")
  List<String> permissionIds;
  
}
