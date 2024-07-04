package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.ClosureReason;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details about the user account closure")
public class CloseUserAccount {

	@Schema(description = "The unique ID of the account")
	private String accountId;

	@Schema(description = "Reasons for closing the account")
	private ClosureReason[] closeReason;
}
