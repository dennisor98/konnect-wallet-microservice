package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinDto {
	@Pattern(regexp = "^[0-9]{4}$", message = "Please enter a valid four-digit number")
	@NotNull(message = "Pin required")

	public String pin;

}
