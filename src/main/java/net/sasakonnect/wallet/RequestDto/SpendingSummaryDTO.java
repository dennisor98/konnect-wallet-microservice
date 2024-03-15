package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SpendingSummaryDTO {
  @NotNull
  @NotEmpty
  String year;
  
  @NotNull
  @NotEmpty
  String month;
}
