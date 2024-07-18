package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SmePasswordDto {
	@NotBlank(message = "Password must be provided")
	String oldPassword;
	
	@NotBlank(message = "Password must be provided")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*\\d)(?=\\S+$).{8,}$",
        message = "Password must be at least 8 characters long, contain at least one uppercase letter, one special character, and one numeric value"
    )
    private String password;
  
}
