package net.sasakonnect.wallet.RequestDto.account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.IdentityType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmAccDTO {
   @NotNull(message="provide identity source")
   IdentityType identityType;
   
   @NotNull(message="provide an answer")
   String answer;
}
