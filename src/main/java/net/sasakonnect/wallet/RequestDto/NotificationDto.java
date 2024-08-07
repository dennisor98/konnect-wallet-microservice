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
   @NotNull(message="title is required")
   String title;
   
   @NotNull(message="message is required")
   String message;
   
   @NotNull(message="contentType is required")
   String contentType;
   
   @NotNull(message="targetType is required")
   NotificationTargetType targetType;
   
   String caption;
   
   String userId;
   
}
