package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class WalletTransferDto {
	// your phone number format
	@NotEmpty(message = "Phone number is required")
	private String phoneNumber;
	@Builder.Default
	String currencyCode = "KES";
	@Pattern(regexp = "^\\d*\\.?\\d+$", message = "Invalid amount format") // Use an appropriate regular expression for
																			// your amount format
	@NotEmpty(message = "Amount is required")
	String amount;
	String remarks;
	@Builder.Default
	String otpType = "SMS";
	String payeeMobileForNotification;

}
