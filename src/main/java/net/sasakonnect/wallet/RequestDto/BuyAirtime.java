package net.sasakonnect.wallet.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyAirtime {
	String accountId;
	String mobileNumber;
	String networkProvider;
	String amount;

	public Integer getNetworkProviderId() {
		switch (this.networkProvider) {
		case "SAFARICOM": {
			return 0;
		}
		case "AIRTEL": {
			return 1;

		}
		case "TELKOM": {
			return 2;
		}
		}
		return 0;
	}

}
