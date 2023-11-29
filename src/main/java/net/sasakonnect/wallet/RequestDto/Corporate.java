package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Corporate {
	@NotNull(message="userId param is required")
	private String userId;
	
    @NotBlank(message = "Corporate email cannot be blank")
    @Email(message = "Invalid corporate email format")
    private String corporateEmail;
  
    @NotBlank(message = "Phone cannot be blank")
    private String phone;

    @NotNull(message = "isVerified cannot be null")
    private Boolean isVerified;

    @NotNull(message = "isEmailVerified cannot be null")
    private Boolean isEmailVerified;

    @NotNull(message = "isActive cannot be null")
    private Boolean isActive;
    
}
