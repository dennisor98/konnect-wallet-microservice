package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.RequestDto.MpesaBillType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeMpesaBilling {
	@NotNull
	public MpesaBillType billType;
	
	@NotNull
	String payerAccountNumber;

	@NotBlank
	public String shortCode;

	@Nullable
	public String receivingAccount;

	@Positive
	public Integer amount;

	@Nullable
	public String shortNote;

	public String otpType = "SMS";
}
