package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayUtility {
	@Schema(hidden = true)
	String accountId;
	String billOrderNumber;
	@Parameter(description = "type of utitility", schema = @Schema(allowableValues = { "DSTV", "GOTV", "KWESE",
			"STARTIMES", "WATER" }))

	UtilityBillType billType;
	String amount;
	
	public UtilityBillType getBillTypeStr() {
		return this.billType;
	}

	public Integer getBillType() {
		switch (this.billType) {
		case DSTV:
			return 0;
		case GOTV:
			return 1;
		case KWESE:
			return 2;
		case STARTIMES:
			return 3;
		case WATER:
			return 6;
		default:
			return 0;

		}
	}

}
