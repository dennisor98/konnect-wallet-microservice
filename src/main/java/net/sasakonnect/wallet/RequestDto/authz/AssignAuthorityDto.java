package net.sasakonnect.wallet.RequestDto.authz;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignAuthorityDto {
   @NotNull(message="clientId is missing")
   String clientId;
   
   @NotNull(message="authorityIds is missing")
   List<Authority> authorities;
   
}





