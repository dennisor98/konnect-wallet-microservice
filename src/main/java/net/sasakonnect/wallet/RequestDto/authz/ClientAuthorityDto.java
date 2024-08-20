package net.sasakonnect.wallet.RequestDto.authz;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientAuthorityDto {
	@NotNull(message="clientId is missing")
   String clientId;
	
	@NotNull(message="claimName is missing")
	String authName;
	
	String description;
	
}
