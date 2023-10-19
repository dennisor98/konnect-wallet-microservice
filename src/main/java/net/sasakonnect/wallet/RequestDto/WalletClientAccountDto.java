package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.FinancialInstituation;

@Data
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class WalletClientAccountDto {

	private FinancialInstituation accountType;
	private String paybillNumber;
	private String tillNumber;
	private String payBillAccountNo;
	private String bankAccountNo;
	private String walletAccountNo;
	private String bankCode;
	@NotNull(message = "App Id required")
	private String appId;

	@AssertTrue(message = "Either paybillNumber or tillNumber must be provided when accountType is M-Pesa")
	private boolean isMpesaValid() {

		if (FinancialInstituation.MPESA.equals(accountType)) {
			return paybillNumber != null || tillNumber != null;
		}
		return true; // Validation passes for other account types
	}

	@AssertTrue(message = "Please provide  bankcode and  account No")
	private boolean isBankDataValid() {

		if (FinancialInstituation.BANK.equals(accountType)) {
			return bankCode != null || bankAccountNo != null;
		}
		return true; // Validation passes for other account types
	}

	@AssertTrue(message = "Please provide  Wallet Account to receive")
	private boolean isWalletAcccountValid() {

		if (FinancialInstituation.WALLET.equals(accountType)) {
			return walletAccountNo != null;
		}
		return true; // Validation passes for other account types
	}

}
