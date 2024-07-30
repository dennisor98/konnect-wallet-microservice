package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeAssignRoleDto {
   @NotBlank(message="user_id is required")
   String user_id;
   
   @NotBlank(message="user_id is required")
   String role_id;
   
}
