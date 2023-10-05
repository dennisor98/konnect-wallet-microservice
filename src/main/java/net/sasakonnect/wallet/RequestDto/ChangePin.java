package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data

public class ChangePin extends PinDto {
	@Pattern(regexp = "^[0-9]{4}$", message = "Please enter a valid four-digit number")
	@NotNull(message = "Pin required")

	public String oldPin;
}
