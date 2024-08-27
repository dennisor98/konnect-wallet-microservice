package net.sasakonnect.wallet.RequestDto.authz;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Authority {
	@NotNull(message="authId is missing")
	public String authId;

	@NotNull(message="isOptional is missing")
	public Boolean isOptional;
}