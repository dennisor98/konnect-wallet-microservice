package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordResetDto {
	@NotNull(message="oldPassword is required")
	String oldPassword;
	
	@NotNull(message="newPassword is required")
	String newPassword;
}
