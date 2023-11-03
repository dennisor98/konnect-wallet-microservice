package net.sasakonnect.wallet.RequestDto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckUserAccount {
	@NotNull(message = "Pin required")
	String phoneNumber;

}
