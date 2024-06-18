package net.sasakonnect.wallet.RequestDto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateEmailDto {
	@NotNull(message = "provide an  a correct email address email ")
	@Email
	String email;

}
