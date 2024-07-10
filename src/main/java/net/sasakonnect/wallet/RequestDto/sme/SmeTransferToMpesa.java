package net.sasakonnect.wallet.RequestDto.sme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeTransferToMpesa {
	String payerAccountNumber;
	String receiverMobileNumber;
	@Builder.Default
	String currencyCode = "KES";
	String amount;
	String remarks;
	String payeeMobileForNotification;
}
