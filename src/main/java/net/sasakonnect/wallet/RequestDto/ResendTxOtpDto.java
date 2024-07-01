package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendTxOtpDto {
  @NotBlank(message="transactionId cannot be null")
  String transactionId;
}
