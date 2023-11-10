
package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.annotations.PhoneCheck;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PhoneCheckDto {
	// your phone number format
	@NotEmpty(message = "Phone number is required")
	@PhoneCheck
	private String phoneNumber;

}
