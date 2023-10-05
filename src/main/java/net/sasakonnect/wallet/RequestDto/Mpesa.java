package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mpesa {
	@NotNull
	String mpesaNumber;

	@NotNull
	@Digits(integer = Integer.MAX_VALUE, fraction = 0)
	@Min(value = 10, message = "Amount must be greater than or equal to 10")
	Integer amount;

}
