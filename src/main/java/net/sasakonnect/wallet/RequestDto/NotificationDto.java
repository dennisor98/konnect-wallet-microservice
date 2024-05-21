package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.NotificationTargetType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
   @NotNull()
   String title;
   
   @NotNull()
   String message;
   
   @NotNull()
   NotificationTargetType targetType;
   
   String userId;
   
}
