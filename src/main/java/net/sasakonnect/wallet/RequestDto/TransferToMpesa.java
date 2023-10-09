package net.sasakonnect.wallet.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferToMpesa {
	String bankCode;
	String receiverMobileNumber;
	@Builder.Default
	String currencyCode = "KES";
	String amount;
	String remarks;
	@Builder.Default

	String otpType = "OTP";
	String payeeMobileForNotification;

}
