package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientTransReqDto {
	@NotNull(message="clientPublicKey is missing")
	String clientPublicKey;
   @NotNull(message="startDate is missing")
   String startDate;
   @NotNull(message="endDate is missing")
   String endDate;
   Integer pageNumber;
   Integer pageSize;
}
