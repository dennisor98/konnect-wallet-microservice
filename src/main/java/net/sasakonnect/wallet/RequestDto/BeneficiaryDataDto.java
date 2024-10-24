package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.PayType;
import net.sasakonnect.wallet.enums.PayeeType;
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public class BeneficiaryDataDto {
		@NotNull(message="bankCode is missing")
		String bankCode;

		@NotNull(message="receiverName is missing")
		String receiverName;
		@NotNull(message="receiverAccount is missing")
		String receiverAccount;
		PayeeType payeeType;
		PayType payType;
		String subAccount;
		@Builder.Default
		String currencyCode = "KES";
		@NotNull(message="amount must be specified")
		String amount;
		String remarks;
		@Builder.Default
		String otpType = "SMS";
		String payeeMobileForNotification;
	}