package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SmeMpesa {
	@NotNull
	String mpesaNumber;
     
	@NotNull
	String accountNumber;
	
	@NotNull
	@Digits(integer = Integer.MAX_VALUE, fraction = 0)
	@Min(value = 10, message = "Amount must be greater than or equal to 10")
	Integer amount;
}
