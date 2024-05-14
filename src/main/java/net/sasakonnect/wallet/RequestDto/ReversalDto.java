package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReversalDto {
   @NotNull()
   String accountNumber;
   
   @NotNull()
   String mobileNumber;
   
   @NotNull()
   String reference;
   
   @NotNull()
   String description;
}
