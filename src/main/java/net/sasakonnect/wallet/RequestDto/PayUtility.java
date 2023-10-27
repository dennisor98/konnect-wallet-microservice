package net.sasakonnect.wallet.RequestDto;

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
	@Schema(hidden = true)

	String billType;
	String amount;

	public Integer getBillType() {
		switch (this.billType) {
		case "DSTV": {
			return 0;
		}
		case "GOTV": {
			return 1;

		}
		case "KWESE": {
			return 2;
		}
		case "STARTIMES": {
			return 3;
		}
		case "WATER": {
			return 6;
		}
		}
		return 0;
	}

}
