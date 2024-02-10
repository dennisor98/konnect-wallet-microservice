package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.annotations.Base64ImageValidator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpgradeWalletAccountDto {

	@NotNull(message = "kraPin is required")
	private String kraPin;

	@NotNull(message = "frontSidePhoto  is required")
	@Base64ImageValidator
	private String frontSidePhoto;

	@NotNull(message = "selfiePhoto is required")
	@Base64ImageValidator
	private String selfiePhoto;

	@NotNull(message = "backSide of id is required")
	@Base64ImageValidator
	private String backSidePhoto;

}
