package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientTransReqDto {
	@NotNull(message="clientPublicKey is missing")
	String clientPublicKey;
}
