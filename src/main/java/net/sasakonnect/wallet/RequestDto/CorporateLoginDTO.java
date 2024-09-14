package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorporateLoginDTO {
	@NotNull(message = "Phone number is required")
	private String phoneNumber;
	
	private String messageSignature;
	@Nullable
	@Builder.Default
	@Schema(hidden = true) // Exclude this property from documentation

	private String countryCode = "254";

	@Schema(hidden = true) // Exclude this property from documentation

	public String getFullPhone() {
		return this.countryCode + this.getSerchablePhone();
	}

	@Schema(hidden = true)
	public String getSerchablePhone() {
		var phone_length = phoneNumber.length();
		if (phone_length > 9) {
			return phoneNumber.substring(phone_length - 9);
		}
		return this.phoneNumber;
	}
  
}
