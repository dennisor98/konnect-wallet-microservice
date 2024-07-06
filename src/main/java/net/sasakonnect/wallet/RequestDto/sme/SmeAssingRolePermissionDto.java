package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeAssingRolePermissionDto {
   @NotBlank(message="role_id is required")
   String role_id;
   
   @NotBlank(message="permissionIds is required")
   String[] permissionIds;
}
