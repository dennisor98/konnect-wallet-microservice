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
public class RoleDTO {
 @NotNull(message="Role name cannot be null")
 String rolename;
 @NotNull(message="Description   cannot be null")
 String roleDescription;
 

 
}
