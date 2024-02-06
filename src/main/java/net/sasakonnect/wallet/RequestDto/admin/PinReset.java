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
public class PinReset {
@NotNull(message="user id cannot be null")
String userId;

@NotNull(message="counter cannot be null")
Integer counter;
}
