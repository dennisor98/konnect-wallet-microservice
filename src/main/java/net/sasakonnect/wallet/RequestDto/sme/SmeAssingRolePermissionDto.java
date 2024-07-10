package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SmeAssingRolePermissionDto {
   @NotBlank(message="role_id is required")
   String role_id;
   
   @NotEmpty(message="permissionIds is required")
   String[] permissionIds;
}
