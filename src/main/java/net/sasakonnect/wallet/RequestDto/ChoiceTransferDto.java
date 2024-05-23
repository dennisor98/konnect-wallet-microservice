package net.sasakonnect.wallet.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ChoiceTransferDto {
	String bankCode;
	String receiverName;
	String receiverAccount;
	@Builder.Default
	String currencyCode = "KES";
	String amount;
	String remarks;
	@Builder.Default
	String otpType = "SMS";
	String payeeMobileForNotification;
}
