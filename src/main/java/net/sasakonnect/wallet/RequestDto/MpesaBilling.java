package net.sasakonnect.wallet.RequestDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MpesaBilling {
	@NotNull
	public MpesaBillType billType;

	@NotBlank
	public String shortCode;

	@Nullable
	public String receivingAccount;

	@Positive
	public Integer amount;

	@NotBlank
	public String shortNote;

	public String otpType = "SMS";
}
