package net.sasakonnect.wallet.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayUtility {
	String accountId;
	String billOrderNumber;
	String billType;
	String amount;

	public Integer getNetworkProviderId() {
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
