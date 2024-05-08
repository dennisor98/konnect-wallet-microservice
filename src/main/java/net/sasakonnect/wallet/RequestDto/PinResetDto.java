package net.sasakonnect.wallet.RequestDto;

import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
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
  String idNumber;
   
  @NotNull()
  String mobileNumber;
  
  @NotNull()
  String firstName;
  
  @NotNull()
  String lastName;
  
  @NotNull()
  Date dateofBirth;
  
  @NotNull()
  Double accountBalance;
  
  @NotNull()
  Double lastReceivedAmount;
  
  @NotNull()
  Double lastSentAmount;
  
  @NotNull()
  PinResetType resetReason;
  
  
  @NotNull()
  String approver;
  
  
  
}
