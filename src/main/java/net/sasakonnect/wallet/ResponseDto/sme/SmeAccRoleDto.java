package net.sasakonnect.wallet.ResponseDto.sme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SmeAccRoleDto {
	@NotBlank(message="accountId cannot be blank")
	String accountId;

	@NotBlank(message="name cannot be blank")
	String name;

	@NotBlank(message="description cannot be blank")
	String description;

}
