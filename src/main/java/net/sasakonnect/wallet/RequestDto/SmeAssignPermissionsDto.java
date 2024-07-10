package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeAssignPermissionsDto {
	@NotBlank(message="roleId cannot be empty")
	String roleId;
  
	@NotBlank(message="permissionIds cannot be empty")
	String[] permissionIds;
  
}
