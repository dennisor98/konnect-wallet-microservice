package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class SdkSearchCustomer {
	 @NotNull(message = "Country code required")
	    @Pattern(regexp = "^\\d{0,3}$", message = "At most 3 digits")
	    @Schema(description = "Country code of the customer", example = "123")
	    private String countryCode;

	    @NotNull(message = "Pin required")
	    @Pattern(regexp = "^[0-9]{9}$", message = "Please enter a valid 9 digit number")
	    @Schema(description = "Phone number of the customer", example = "123456789")
	    private String phoneNumber;

}
