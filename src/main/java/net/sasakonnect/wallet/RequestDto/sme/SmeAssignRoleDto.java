package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmeAssignRoleDto {
   @NotBlank(message="user_id is required")
   String user_id;
   
   @NotBlank(message="user_id is required")
   String role_id;
   
}
