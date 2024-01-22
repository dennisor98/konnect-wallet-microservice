package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotEmpty;
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
 @NotEmpty(message="Role cannot be empty")
 String rolename;
 @NotEmpty(message="Description   cannot be null")
 @NotNull(message="Description   cannot be null")
 String roleDescription;
 

 
}
