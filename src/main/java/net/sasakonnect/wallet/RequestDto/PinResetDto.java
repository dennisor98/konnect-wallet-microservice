package net.sasakonnect.wallet.RequestDto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.PinResetType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinResetDto {
	@NotNull()
    private String idNumber;
   
    @NotNull()
    private String mobileNumber;
  
    @NotNull()
    private String firstName;
  
    @NotNull()
    private String lastName;
  
    // You can uncomment this if you want to validate dateofBirth
    // @NotNull
    // private Date dateofBirth;
  
    @NotNull()
    private float balance;
  
    @NotNull()
    private float lastReceivedAmount;
  
    @NotNull()
    private float lastSentAmount;
  
    @NotNull()
    private PinResetType resetReason;
  
    @NotNull()
    private String approver;
}
