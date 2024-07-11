package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.RequestDto.ChoiceTransferDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceSmeTransferDto {
	@NotNull
	String payerAccountNumber;
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
